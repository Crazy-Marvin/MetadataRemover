package rocks.poopjournal.metadataremover.metadata.handlers

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.poifs.filesystem.OfficeXmlFileException
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.odftoolkit.simple.Document
import org.odftoolkit.simple.SpreadsheetDocument
import org.odftoolkit.simple.TextDocument
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.metadata.MetadataHandler
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.model.resources.MediaTypes
import rocks.poopjournal.metadataremover.model.resources.Text
import rocks.poopjournal.metadataremover.util.extensions.toCalendar
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.Optional

class DocumentMetadataHandler(private val context: Context) : MetadataHandler {

    override val readableMimeTypes =    MediaTypes[MediaTypes.MICROSOFT_WORD] +
                                        MediaTypes[MediaTypes.OOXML_DOCUMENT] +
                                        MediaTypes[MediaTypes.MICROSOFT_EXCEL] +
                                        MediaTypes[MediaTypes.OOXML_SHEET] +
                                        MediaTypes[MediaTypes.OPENDOCUMENT_TEXT] +
                                        MediaTypes[MediaTypes.OPENDOCUMENT_SPREADSHEET] +
                                        MediaTypes[MediaTypes.PDF]

    override val writableMimeTypes =    MediaTypes[MediaTypes.MICROSOFT_WORD] +
                                        MediaTypes[MediaTypes.OOXML_DOCUMENT] +
                                        MediaTypes[MediaTypes.MICROSOFT_EXCEL] +
                                        MediaTypes[MediaTypes.OOXML_SHEET] +
                                        MediaTypes[MediaTypes.OPENDOCUMENT_TEXT] +
                                        MediaTypes[MediaTypes.OPENDOCUMENT_SPREADSHEET] +
                                        MediaTypes[MediaTypes.PDF]


    override suspend fun loadMetadata(mediaType: MediaType, inputFile: File): Metadata? {
        check(mediaType in readableMimeTypes)

        return Metadata(
            thumbnail = Image(inputFile),
            attributes = readDocumentMetadata(context, mediaType, inputFile.toUri()).toSet()
        )
    }

    override suspend fun removeMetadata(
        mediaType: MediaType,
        inputFile: File,
        outputFile: File,
    ): Boolean {
        check(mediaType in writableMimeTypes)

        println(mediaType)

        when (mediaType) {
            MediaTypes.OOXML_DOCUMENT -> removeOOXMLMetadata(inputFile, outputFile, MediaTypes.OOXML_DOCUMENT)
            MediaTypes.OOXML_SHEET -> removeOOXMLMetadata(inputFile, outputFile, MediaTypes.OOXML_SHEET)
            MediaTypes.MICROSOFT_WORD -> removeHWPFMetadata(inputFile, outputFile)
            MediaTypes.MICROSOFT_EXCEL -> removeHSSFMetadata(inputFile, outputFile)
            MediaTypes.OPENDOCUMENT_TEXT -> removeODTMetadata(inputFile, outputFile)
            MediaTypes.OPENDOCUMENT_SPREADSHEET -> removeODSMetadata(inputFile, outputFile)
            MediaTypes.PDF -> removePDFMetadata(inputFile, outputFile)
        }

        return true
    }

    private fun readDocumentMetadata(context: Context, mediaType: MediaType, uri: Uri): List<Metadata.Attribute> {
        val metadataList = mutableListOf<Metadata.Attribute>()

        when (mediaType) {
            MediaTypes.OOXML_DOCUMENT -> readOOXMLMetadata(context, uri, MediaTypes.OOXML_DOCUMENT, metadataList)
            MediaTypes.OOXML_SHEET -> readOOXMLMetadata(context, uri,  MediaTypes.OOXML_SHEET, metadataList)
            MediaTypes.MICROSOFT_WORD -> readHWPFMetadata(context, uri, metadataList)
            MediaTypes.MICROSOFT_EXCEL -> readHSSFMetadata(context, uri, metadataList)
            MediaTypes.OPENDOCUMENT_TEXT, MediaTypes.OPENDOCUMENT_SPREADSHEET -> readODFMetadata(context, uri, metadataList)
            MediaTypes.PDF -> readPDFMetadata(context, uri, metadataList)
            else -> metadataList.add(Metadata.Attribute(label = Text("Unsupported file type"), icon = Image(R.drawable.ic_error), primaryValue = Text(mediaType.type)))
        }

        return metadataList
    }

    //docx, xlsx
    private fun readOOXMLMetadata(context: Context, uri: Uri, mediaType: MediaType, metadataList: MutableList<Metadata.Attribute>) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val document = when(mediaType) {
                MediaTypes.OOXML_DOCUMENT -> XWPFDocument(inputStream)
                MediaTypes.OOXML_SHEET, MediaTypes.MICROSOFT_EXCEL -> XSSFWorkbook(inputStream)
                else -> throw IllegalArgumentException("Unsupported file type")
            }

            val properties = document.properties
            val coreProperties = properties.coreProperties
            val extendedProperties = properties.extendedProperties

            coreProperties.creator?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Author"), icon = Image(R.drawable.ic_author), primaryValue = Text(coreProperties.creator )))
            }

            coreProperties.lastModifiedByUser?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Last Modified By"), icon = Image(R.drawable.ic_edit), primaryValue = Text(coreProperties.lastModifiedByUser )))
            }


            coreProperties.description?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Description"), icon = Image(R.drawable.ic_description), primaryValue = Text(coreProperties.description )))
            }

            coreProperties.subject?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Subject"), icon = Image(R.drawable.ic_subject), primaryValue = Text(coreProperties.subject )))
            }

            coreProperties.created?.let {
                metadataList.add(Metadata.Attribute(label = Text("Created"), icon = Image(R.drawable.ic_calendar_today), primaryValue = Text(convertDate(coreProperties.created))))
            }

            coreProperties.modified?.let {
                metadataList.add(Metadata.Attribute(label = Text("Modified"), icon = Image(R.drawable.ic_update), primaryValue = Text(convertDate(coreProperties.modified))))
            }

            coreProperties.lastPrinted?.let {
                metadataList.add(Metadata.Attribute(label = Text("Last Printed"), icon = Image(R.drawable.ic_update), primaryValue = Text(convertDate(coreProperties.lastPrinted))))
            }

            coreProperties.revision?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Revision"), icon = Image(R.drawable.ic_history), primaryValue = Text(coreProperties.revision )))
            }

            coreProperties.keywords?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Keywords"), icon = Image(R.drawable.ic_label), primaryValue = Text(coreProperties.keywords )))
            }

            coreProperties.category?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Category"), icon = Image(R.drawable.ic_category), primaryValue = Text(coreProperties.category )))
            }

            coreProperties.contentStatus?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Content Status"), icon = Image(R.drawable.ic_info), primaryValue = Text(coreProperties.contentStatus )))
            }

            coreProperties.contentType?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Content Type"), icon = Image(R.drawable.ic_file_type), primaryValue = Text(coreProperties.contentType )))
            }

            extendedProperties.company?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Company"), icon = Image(R.drawable.ic_business), primaryValue = Text(extendedProperties.company )))
            }

            extendedProperties.manager?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Manager"), icon = Image(R.drawable.ic_supervisor_account), primaryValue = Text(extendedProperties.manager )))
            }

            document.close()
        }
    }

    private fun removeOOXMLMetadata(inputFile: File, outputFile: File, mediaType: MediaType){
        val document = when (mediaType) {
            MediaTypes.OOXML_DOCUMENT -> XWPFDocument(FileInputStream(inputFile))
            MediaTypes.OOXML_SHEET, MediaTypes.MICROSOFT_EXCEL-> XSSFWorkbook(FileInputStream(inputFile))
            else -> throw IllegalArgumentException("Unsupported file format")
        }

        val properties = document.properties
        val coreProperties = properties.coreProperties
        val extendedProperties = properties.extendedProperties
        val date = getStartOfTimeDate()

        coreProperties.title = ""
        coreProperties.creator = ""
        coreProperties.lastModifiedByUser = ""
        coreProperties.description = ""
        coreProperties.setSubjectProperty("")
        coreProperties.setCreated(Optional.of(date))
        coreProperties.setModified(Optional.of(date))
        coreProperties.setLastPrinted(Optional.of(date))
        coreProperties.revision = ""
        coreProperties.keywords = ""
        coreProperties.category = ""
        coreProperties.contentStatus = ""
        coreProperties.contentType = ""
        extendedProperties.company = ""
        extendedProperties.manager = ""

        FileOutputStream(outputFile).use { out -> document.write(out) }
        document.close()
    }

    //Doc
    private fun readHWPFMetadata(context: Context, uri: Uri, metadataList: MutableList<Metadata.Attribute>) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val document = HWPFDocument(inputStream)
            val summaryInformation = document.summaryInformation
            val documentSummaryInformation = document.documentSummaryInformation

            summaryInformation.author?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Author"), icon = Image(R.drawable.ic_person), primaryValue = Text(summaryInformation.author )))
            }

            summaryInformation.subject?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Subject"), icon = Image(R.drawable.ic_subject), primaryValue = Text(summaryInformation.subject )))
            }

            summaryInformation.keywords?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Keywords"), icon = Image(R.drawable.ic_label), primaryValue = Text(summaryInformation.keywords )))
            }

            summaryInformation.comments?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Comments"), icon = Image(R.drawable.ic_comment), primaryValue = Text(summaryInformation.comments )))
            }

            summaryInformation.template?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Template"), icon = Image(R.drawable.ic_description), primaryValue = Text(summaryInformation.template )))
            }

            summaryInformation.revNumber?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Revision Number"), icon = Image(R.drawable.ic_history), primaryValue = Text(summaryInformation.revNumber )))
            }

            summaryInformation.lastAuthor?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Last Author"), icon = Image(R.drawable.ic_edit), primaryValue = Text(summaryInformation.lastAuthor )))
            }

            summaryInformation.applicationName?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Application Name"), icon = Image(R.drawable.ic_apps), primaryValue = Text(summaryInformation.applicationName )))
            }

            summaryInformation.createDateTime?.let {
                metadataList.add(Metadata.Attribute(label = Text("Created Date"), icon = Image(R.drawable.ic_calendar_today), primaryValue = Text(convertDate(summaryInformation.createDateTime) )))
            }

            summaryInformation.lastSaveDateTime?.let {
                metadataList.add(Metadata.Attribute(label = Text("Last Saved Date"), icon = Image(R.drawable.ic_update), primaryValue = Text(convertDate(summaryInformation.lastSaveDateTime))))
            }

            documentSummaryInformation.company?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Company"), icon = Image(R.drawable.ic_business), primaryValue = Text(documentSummaryInformation.company )))
            }

            documentSummaryInformation.category?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Category"), icon = Image(R.drawable.ic_category), primaryValue = Text(documentSummaryInformation.category )))
            }

            documentSummaryInformation.manager?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Manager"), icon = Image(R.drawable.ic_supervisor_account), primaryValue = Text(documentSummaryInformation.manager )))
            }

            document.close()
        }
    }

    private fun removeHWPFMetadata(inputFile: File, outputFile: File) {
        val document = HWPFDocument(FileInputStream(inputFile))
        val summaryInformation = document.summaryInformation
        val documentSummaryInformation = document.documentSummaryInformation
        val date = getStartOfTimeDate()

        summaryInformation.title = ""
        summaryInformation.author = ""
        summaryInformation.subject = ""
        summaryInformation.keywords = ""
        summaryInformation.comments = ""
        summaryInformation.template = ""
        summaryInformation.revNumber = ""
        summaryInformation.lastAuthor = ""
        summaryInformation.applicationName = ""
        summaryInformation.createDateTime = date
        summaryInformation.lastSaveDateTime = date
        documentSummaryInformation.company = ""
        documentSummaryInformation.category = ""
        documentSummaryInformation.manager = ""


        FileOutputStream(outputFile).use { out -> document.write(out) }
        document.close()
    }

    //xls
    private fun readHSSFMetadata(context: Context, uri: Uri, metadataList: MutableList<Metadata.Attribute>) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val workbook = HSSFWorkbook(inputStream)
                val summaryInformation = workbook.summaryInformation
                val documentSummaryInformation = workbook.documentSummaryInformation

                summaryInformation.author?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Author"), icon = Image(R.drawable.ic_person), primaryValue = Text(summaryInformation.author)))
                }

                summaryInformation.lastAuthor?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Last Author"), icon = Image(R.drawable.ic_edit), primaryValue = Text(summaryInformation.lastAuthor)))
                }

                summaryInformation.subject?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Subject"), icon = Image(R.drawable.ic_subject), primaryValue = Text(summaryInformation.subject)))
                }

                summaryInformation.keywords?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Keywords"), icon = Image(R.drawable.ic_label), primaryValue = Text(summaryInformation.keywords)))
                }

                documentSummaryInformation.category?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Category"), icon = Image(R.drawable.ic_category), primaryValue = Text(documentSummaryInformation.category)))
                }

                summaryInformation.comments?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Comments"), icon = Image(R.drawable.ic_comment), primaryValue = Text(summaryInformation.comments)))
                }

                summaryInformation.template?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Template"), icon = Image(R.drawable.ic_description), primaryValue = Text(summaryInformation.template)))
                }

                summaryInformation.revNumber?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Revision Number"), icon = Image(R.drawable.ic_history), primaryValue = Text(summaryInformation.revNumber)))
                }

                summaryInformation.applicationName?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Application Name"), icon = Image(R.drawable.ic_apps), primaryValue = Text(summaryInformation.applicationName)))
                }

                summaryInformation.createDateTime?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Created Date"), icon = Image(R.drawable.ic_calendar_today), primaryValue = Text(convertDate(summaryInformation.createDateTime))))
                }

                summaryInformation.lastSaveDateTime?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Last Saved Date"), icon = Image(R.drawable.ic_update), primaryValue = Text(convertDate(summaryInformation.lastSaveDateTime))))
                }

                summaryInformation.lastPrinted?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Last Printed"), icon = Image(R.drawable.ic_print), primaryValue = Text(convertDate(summaryInformation.lastPrinted))))
                }

                documentSummaryInformation.company?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Company"), icon = Image(R.drawable.ic_business), primaryValue = Text(documentSummaryInformation.company)))
                }

                documentSummaryInformation.manager?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(Metadata.Attribute(label = Text("Manager"), icon = Image(R.drawable.ic_supervisor_account), primaryValue = Text(documentSummaryInformation.manager)))
                }

                workbook.close()
            }
        } catch (e: OfficeXmlFileException){
            readOOXMLMetadata(context, uri, MediaTypes.MICROSOFT_EXCEL, metadataList)
        }
    }

    private fun removeHSSFMetadata(inputFile: File, outputFile: File){
        try {
            val workbook = HSSFWorkbook(FileInputStream(inputFile))
            val summaryInformation = workbook.summaryInformation
            val documentSummaryInformation = workbook.documentSummaryInformation
            val date = getStartOfTimeDate()

            summaryInformation.title = ""
            summaryInformation.author = ""
            summaryInformation.lastAuthor = ""
            summaryInformation.subject = ""
            summaryInformation.keywords = ""
            documentSummaryInformation.category = ""
            summaryInformation.comments = ""
            summaryInformation.template = ""
            summaryInformation.revNumber = ""
            summaryInformation.applicationName = ""
            summaryInformation.createDateTime = date
            summaryInformation.lastSaveDateTime = date
            summaryInformation.lastPrinted = date
            documentSummaryInformation.company = ""
            documentSummaryInformation.manager = ""

            FileOutputStream(outputFile).use { out -> workbook.write(out) }
            workbook.close()
        } catch (e: OfficeXmlFileException){
            removeOOXMLMetadata(inputFile, outputFile, MediaTypes.MICROSOFT_EXCEL)
        }
    }

    //Open office
    private fun readODFMetadata(context: Context, uri: Uri, metadataList: MutableList<Metadata.Attribute>) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val document = Document.loadDocument(inputStream)
            val metadata = document.officeMetadata

            metadata.initialCreator?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Initial Creator"), icon = Image(R.drawable.ic_person), primaryValue = Text(metadata.initialCreator)))
            }

            metadata.creator?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Creator"), icon = Image(R.drawable.ic_edit), primaryValue = Text(metadata.creator)))
            }

            metadata.editingCycles.toString().takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Editing Cycles"), icon = Image(R.drawable.ic_loop), primaryValue = Text(metadata.editingCycles.toString())))
            }

            metadata.printedBy?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Printed By"), icon = Image(R.drawable.ic_print), primaryValue = Text(metadata.printedBy)))
            }

            metadata.printDate?.let {
                metadataList.add(Metadata.Attribute(label = Text("Print Date"), icon = Image(R.drawable.ic_print), primaryValue = Text(convertDate(metadata.printDate.time))))
            }

            metadata.dcdate?.let {
                metadataList.add(Metadata.Attribute(label = Text("DC Date"), icon = Image(R.drawable.ic_update), primaryValue = Text(convertDate(metadata.dcdate.time))))
            }

            metadata.creationDate?.let {
                metadataList.add(Metadata.Attribute(label = Text("Created Date"), icon = Image(R.drawable.ic_calendar_today), primaryValue = Text(convertDate(metadata.creationDate.time))))
            }

            metadata.language?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Language"), icon = Image(R.drawable.ic_language), primaryValue = Text(metadata.language)))
            }

            metadata.keywords.toString().takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Keywords"), icon = Image(R.drawable.ic_label), primaryValue = Text(metadata.keywords.toString())))
            }

            metadata.subject?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Subject"), icon = Image(R.drawable.ic_subject), primaryValue = Text(metadata.subject)))
            }

            metadata.description?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Description"), icon = Image(R.drawable.ic_description), primaryValue = Text(metadata.description)))
            }

            metadata.generator?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Generator"), icon = Image(R.drawable.ic_build), primaryValue = Text(metadata.generator)))
            }

            document.close()
        }
    }

    private fun removeODSMetadata(inputFile: File, outputFile: File) {
        val spreadsheet = SpreadsheetDocument.loadDocument(inputFile)
        val metadata = spreadsheet.officeMetadata
        val  date = getStartOfTimeDate()

        metadata.title = ""
        metadata.initialCreator = ""
        metadata.creator = ""
        metadata.editingCycles = 0
        metadata.printedBy = ""
        metadata.printDate = date.toCalendar()
        metadata.dcdate = date.toCalendar()
        metadata.creationDate = date.toCalendar()
        metadata.language = ""
        metadata.keywords = emptyList()
        metadata.subject = ""
        metadata.description = ""
        metadata.generator = ""

        spreadsheet.save(outputFile)
    }

    private fun removeODTMetadata(inputFile: File, outputFile: File) {
        val document = TextDocument.loadDocument(inputFile)
        val metadata = document.officeMetadata
        val  date = getStartOfTimeDate()

        metadata.title = ""
        metadata.initialCreator = ""
        metadata.creator = ""
        metadata.editingCycles = 0
        metadata.printedBy = ""
        metadata.printDate = date.toCalendar()
        metadata.dcdate = date.toCalendar()
        metadata.creationDate = date.toCalendar()
        metadata.language = ""
        metadata.keywords = emptyList()
        metadata.subject = ""
        metadata.description = ""
        metadata.generator = ""

        document.save(outputFile)
    }


    //PDF
    private fun readPDFMetadata(context: Context, uri: Uri, metadataList: MutableList<Metadata.Attribute>) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val reader = PdfReader(inputStream)
            val pdfDocument = PdfDocument(reader)
            val info = pdfDocument.documentInfo

            info.author?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Author"), icon = Image(R.drawable.ic_person), primaryValue = Text(info.author)))
            }

            info.subject?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Subject"), icon = Image(R.drawable.ic_subject), primaryValue = Text(info.subject)))
            }

            info.keywords?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Keywords"), icon = Image(R.drawable.ic_label), primaryValue = Text(info.keywords)))
            }

            info.creator?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(Metadata.Attribute(label = Text("Creator"), icon = Image(R.drawable.ic_build), primaryValue = Text(info.creator)))
            }

            pdfDocument.close()
            reader.close()
        }
    }

    private fun removePDFMetadata(inputFile: File, outputFile: File) {
        val tempFile = File(outputFile.absolutePath + ".temp")
        PdfDocument(PdfReader(inputFile), PdfWriter(tempFile)).use { pdfDoc ->
            pdfDoc.documentInfo.apply {
                author = ""
                title = ""
                subject = ""
                keywords = ""
                creator = ""
            }
        }
        tempFile.renameTo(outputFile)
    }

    private fun convertDate(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
        return formatter.format(date)
    }

    private fun getStartOfTimeDate(): Date {
        val calendar = GregorianCalendar(1970, 0, 1)
        return calendar.time
    }
}
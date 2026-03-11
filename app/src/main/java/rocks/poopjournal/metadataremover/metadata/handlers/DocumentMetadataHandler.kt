package rocks.poopjournal.metadataremover.metadata.handlers

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDDocumentInformation
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

    override val readableMimeTypes = MediaTypes[MediaTypes.MICROSOFT_WORD] +
            MediaTypes[MediaTypes.OOXML_DOCUMENT] +
            MediaTypes[MediaTypes.MICROSOFT_EXCEL] +
            MediaTypes[MediaTypes.OOXML_SHEET] +
            MediaTypes[MediaTypes.OPENDOCUMENT_TEXT] +
            MediaTypes[MediaTypes.OPENDOCUMENT_SPREADSHEET] +
            MediaTypes[MediaTypes.PDF]

    override val writableMimeTypes = MediaTypes[MediaTypes.MICROSOFT_WORD] +
            MediaTypes[MediaTypes.OOXML_DOCUMENT] +
            MediaTypes[MediaTypes.MICROSOFT_EXCEL] +
            MediaTypes[MediaTypes.OOXML_SHEET] +
            MediaTypes[MediaTypes.OPENDOCUMENT_TEXT] +
            MediaTypes[MediaTypes.OPENDOCUMENT_SPREADSHEET] +
            MediaTypes[MediaTypes.PDF]


    override suspend fun loadMetadata(
        mediaType: MediaType,
        inputFile: File
    ): Metadata? {

        check(mediaType in readableMimeTypes)

        return Metadata(
            thumbnail = Image(inputFile),
            attributes = readDocumentMetadata(
                context,
                mediaType,
                inputFile.toUri()
            ).toSet()
        )
    }

    override suspend fun removeMetadata(
        mediaType: MediaType,
        inputFile: File,
        outputFile: File,
        attributes: List<Metadata.Attribute>
    ): Boolean {

        check(mediaType in writableMimeTypes)

        val selectedTags = attributes
            .filter { it.removable && it.selected }
            .mapNotNull { it.tag }

        val tagsToRemove =
            if (selectedTags.isEmpty()) null else selectedTags

        when (mediaType) {

            MediaTypes.OOXML_DOCUMENT ->
                removeOOXMLMetadata(inputFile, outputFile, MediaTypes.OOXML_DOCUMENT, tagsToRemove)

            MediaTypes.OOXML_SHEET ->
                removeOOXMLMetadata(inputFile, outputFile, MediaTypes.OOXML_SHEET, tagsToRemove)

            MediaTypes.MICROSOFT_WORD ->
                removeHWPFMetadata(inputFile, outputFile, tagsToRemove)

            MediaTypes.MICROSOFT_EXCEL ->
                removeHSSFMetadata(inputFile, outputFile, tagsToRemove)

            MediaTypes.OPENDOCUMENT_TEXT ->
                removeODTMetadata(inputFile, outputFile, tagsToRemove)

            MediaTypes.OPENDOCUMENT_SPREADSHEET ->
                removeODSMetadata(inputFile, outputFile, tagsToRemove)

            MediaTypes.PDF ->
                removePDFMetadata(inputFile, outputFile, tagsToRemove)
        }

        return true
    }

    private fun readDocumentMetadata(
        context: Context,
        mediaType: MediaType,
        uri: Uri
    ): List<Metadata.Attribute> {
        val metadataList = mutableListOf<Metadata.Attribute>()

        when (mediaType) {
            MediaTypes.OOXML_DOCUMENT -> readOOXMLMetadata(
                context,
                uri,
                MediaTypes.OOXML_DOCUMENT,
                metadataList
            )

            MediaTypes.OOXML_SHEET -> readOOXMLMetadata(
                context,
                uri,
                MediaTypes.OOXML_SHEET,
                metadataList
            )

            MediaTypes.MICROSOFT_WORD -> readHWPFMetadata(context, uri, metadataList)
            MediaTypes.MICROSOFT_EXCEL -> readHSSFMetadata(context, uri, metadataList)
            MediaTypes.OPENDOCUMENT_TEXT, MediaTypes.OPENDOCUMENT_SPREADSHEET -> readODFMetadata(
                context,
                uri,
                metadataList
            )

            MediaTypes.PDF -> readPDFMetadata(context, uri, metadataList)
            else -> metadataList.add(
                Metadata.Attribute(
                    label = Text("Unsupported file type"),
                    icon = Image(R.drawable.ic_error),
                    primaryValue = Text(mediaType.type)
                )
            )
        }

        return metadataList
    }

    //docx, xlsx
    private fun readOOXMLMetadata(
        context: Context,
        uri: Uri,
        mediaType: MediaType,
        metadataList: MutableList<Metadata.Attribute>
    ) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val document = when (mediaType) {
                MediaTypes.OOXML_DOCUMENT -> XWPFDocument(inputStream)
                MediaTypes.OOXML_SHEET, MediaTypes.MICROSOFT_EXCEL -> XSSFWorkbook(inputStream)
                else -> throw IllegalArgumentException("Unsupported file type")
            }

            val properties = document.properties
            val coreProperties = properties.coreProperties
            val extendedProperties = properties.extendedProperties

            coreProperties.creator?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Author"),
                        icon = Image(R.drawable.ic_author),
                        primaryValue = Text(coreProperties.creator),
                        removable = true,
                        tag = "author"
                    )
                )
            }

            coreProperties.lastModifiedByUser?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Last Modified By"),
                        icon = Image(R.drawable.ic_edit),
                        primaryValue = Text(coreProperties.lastModifiedByUser),
                        removable = true,
                        tag = "lastModifiedByUser"
                    )
                )
            }


            coreProperties.description?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Description"),
                        icon = Image(R.drawable.ic_description),
                        primaryValue = Text(coreProperties.description),
                        removable = true,
                        tag = "description"
                    )
                )
            }

            coreProperties.subject?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Subject"),
                        icon = Image(R.drawable.ic_subject),
                        primaryValue = Text(coreProperties.subject),
                        removable = true,
                        tag = "subject"
                    )
                )
            }

            coreProperties.created?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Created"),
                        icon = Image(R.drawable.ic_calendar_today),
                        primaryValue = Text(convertDate(coreProperties.created)),
                        removable = true,
                        tag = "created"
                    )
                )
            }

            coreProperties.modified?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Modified"),
                        icon = Image(R.drawable.ic_update),
                        primaryValue = Text(convertDate(coreProperties.modified)),
                        removable = true,
                        tag = "modified"
                    )
                )
            }

            coreProperties.lastPrinted?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Last Printed"),
                        icon = Image(R.drawable.ic_update),
                        primaryValue = Text(convertDate(coreProperties.lastPrinted)),
                        removable = true,
                        tag = "lastPrinted"
                    )
                )
            }

            coreProperties.revision?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Revision"),
                        icon = Image(R.drawable.ic_history),
                        primaryValue = Text(coreProperties.revision),
                        removable = true,
                        tag = "revision"
                    )
                )
            }

            coreProperties.keywords?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Keywords"),
                        icon = Image(R.drawable.ic_label),
                        primaryValue = Text(coreProperties.keywords),
                        removable = true,
                        tag = "keywords"
                    )
                )
            }

            coreProperties.category?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Category"),
                        icon = Image(R.drawable.ic_category),
                        primaryValue = Text(coreProperties.category),
                        removable = true,
                        tag = "category"
                    )
                )
            }

            coreProperties.contentStatus?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Content Status"),
                        icon = Image(R.drawable.ic_info),
                        primaryValue = Text(coreProperties.contentStatus),
                        removable = true,
                        tag = "contentStatus"
                    )
                )
            }

            coreProperties.contentType?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Content Type"),
                        icon = Image(R.drawable.ic_file_type),
                        primaryValue = Text(coreProperties.contentType),
                        removable = true,
                        tag = "contentType"
                    )
                )
            }

            extendedProperties.company?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Company"),
                        icon = Image(R.drawable.ic_business),
                        primaryValue = Text(extendedProperties.company),
                        removable = true,
                        tag = "company"
                    )
                )
            }

            extendedProperties.manager?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Manager"),
                        icon = Image(R.drawable.ic_supervisor_account),
                        primaryValue = Text(extendedProperties.manager),
                        removable = true,
                        tag = "manager"
                    )
                )
            }

            document.close()
        }
    }

    private fun removeOOXMLMetadata(
        inputFile: File,
        outputFile: File,
        mediaType: MediaType,
        tags: List<String>?
    ) {

        val document =
            if (mediaType == MediaTypes.OOXML_DOCUMENT)
                XWPFDocument(FileInputStream(inputFile))
            else
                XSSFWorkbook(FileInputStream(inputFile))

        val core = document.properties.coreProperties
        val ext = document.properties.extendedProperties
        val date = getStartOfTimeDate()

        val removeAll = tags == null

        if (removeAll || "author" in tags) core.creator = ""
        if (removeAll || "lastModifiedBy" in tags) core.lastModifiedByUser = ""
        if (removeAll || "description" in tags) core.description = ""
        if (removeAll || "subject" in tags) core.setSubjectProperty("")
        if (removeAll || "created" in tags) core.setCreated(Optional.of(date))
        if (removeAll || "modified" in tags) core.setModified(Optional.of(date))
        if (removeAll || "keywords" in tags) core.keywords = ""
        if (removeAll || "category" in tags) core.category = ""
        if (removeAll || "company" in tags) ext.company = ""
        if (removeAll || "manager" in tags) ext.manager = ""

        FileOutputStream(outputFile).use {
            document.write(it)
        }

        document.close()
    }

    //Doc
    private fun readHWPFMetadata(
        context: Context,
        uri: Uri,
        metadataList: MutableList<Metadata.Attribute>
    ) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val document = HWPFDocument(inputStream)
            val summaryInformation = document.summaryInformation
            val documentSummaryInformation = document.documentSummaryInformation

            summaryInformation.author?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Author"),
                        icon = Image(R.drawable.ic_person),
                        primaryValue = Text(summaryInformation.author),
                        removable = true,
                        tag = "author"
                    )
                )
            }

            summaryInformation.subject?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Subject"),
                        icon = Image(R.drawable.ic_subject),
                        primaryValue = Text(summaryInformation.subject),
                        removable = true,
                        tag = "subject"
                    )
                )
            }

            summaryInformation.keywords?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Keywords"),
                        icon = Image(R.drawable.ic_label),
                        primaryValue = Text(summaryInformation.keywords),
                        removable = true,
                        tag = "keywords"
                    )
                )
            }

            summaryInformation.comments?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Comments"),
                        icon = Image(R.drawable.ic_comment),
                        primaryValue = Text(summaryInformation.comments),
                        removable = true,
                        tag = "comments"
                    )
                )
            }

            summaryInformation.template?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Template"),
                        icon = Image(R.drawable.ic_description),
                        primaryValue = Text(summaryInformation.template),
                        removable = true,
                        tag = "template"
                    )
                )
            }

            summaryInformation.revNumber?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Revision Number"),
                        icon = Image(R.drawable.ic_history),
                        primaryValue = Text(summaryInformation.revNumber),
                        removable = true,
                        tag = "revNumber"
                    )
                )
            }

            summaryInformation.lastAuthor?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Last Author"),
                        icon = Image(R.drawable.ic_edit),
                        primaryValue = Text(summaryInformation.lastAuthor),
                        removable = true,
                        tag = "lastAuthor"
                    )
                )
            }

            summaryInformation.applicationName?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Application Name"),
                        icon = Image(R.drawable.ic_apps),
                        primaryValue = Text(summaryInformation.applicationName),
                        removable = true,
                        tag = "applicationName"
                    )
                )
            }

            summaryInformation.createDateTime?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Created Date"),
                        icon = Image(R.drawable.ic_calendar_today),
                        primaryValue = Text(convertDate(summaryInformation.createDateTime)),
                        removable = true,
                        tag = "createDateTime"
                    )
                )
            }

            summaryInformation.lastSaveDateTime?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Last Saved Date"),
                        icon = Image(R.drawable.ic_update),
                        primaryValue = Text(convertDate(summaryInformation.lastSaveDateTime)),
                        removable = true,
                        tag = "lastSaveDateTime"
                    )
                )
            }

            documentSummaryInformation.company?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Company"),
                        icon = Image(R.drawable.ic_business),
                        primaryValue = Text(documentSummaryInformation.company),
                        removable = true,
                        tag = "company"
                    )
                )
            }

            documentSummaryInformation.category?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Category"),
                        icon = Image(R.drawable.ic_category),
                        primaryValue = Text(documentSummaryInformation.category),
                        removable = true,
                        tag = "category"
                    )
                )
            }

            documentSummaryInformation.manager?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Manager"),
                        icon = Image(R.drawable.ic_supervisor_account),
                        primaryValue = Text(documentSummaryInformation.manager),
                        removable = true,
                        tag = "manager"
                    )
                )
            }

            document.close()
        }
    }

    private fun removeHWPFMetadata(inputFile: File, outputFile: File, tags: List<String>?) {
        val document = HWPFDocument(FileInputStream(inputFile))
        val summaryInformation = document.summaryInformation
        val documentSummaryInformation = document.documentSummaryInformation
        val date = getStartOfTimeDate()
        val removeAll = tags == null

       summaryInformation.title = ""
        if (removeAll || "author" in tags) summaryInformation.author = ""
        if (removeAll || "subject" in tags) summaryInformation.subject = ""
        if (removeAll || "keywords" in tags) summaryInformation.keywords = ""
        if (removeAll || "comments" in tags) summaryInformation.comments = ""
        if (removeAll || "template" in tags) summaryInformation.template = ""
        if (removeAll || "revNumber" in tags) summaryInformation.revNumber = ""
        if (removeAll || "lastAuthor" in tags) summaryInformation.lastAuthor = ""
        if (removeAll || "applicationName" in tags) summaryInformation.applicationName = ""
        if (removeAll || "createDateTime" in tags) summaryInformation.createDateTime = date
        if (removeAll || "lastSaveDateTime" in tags) summaryInformation.lastSaveDateTime = date
        if (removeAll || "company" in tags) documentSummaryInformation.company = ""
        if (removeAll || "category" in tags) documentSummaryInformation.category = ""
        if (removeAll || "manager" in tags) documentSummaryInformation.manager = ""


        FileOutputStream(outputFile).use { out -> document.write(out) }
        document.close()
    }

    //xls
    private fun readHSSFMetadata(
        context: Context,
        uri: Uri,
        metadataList: MutableList<Metadata.Attribute>
    ) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val workbook = HSSFWorkbook(inputStream)
                val summaryInformation = workbook.summaryInformation
                val documentSummaryInformation = workbook.documentSummaryInformation

                summaryInformation.author?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Author"),
                            icon = Image(R.drawable.ic_person),
                            primaryValue = Text(summaryInformation.author),
                            removable = true,
                            tag = "author"
                        )
                    )
                }

                summaryInformation.lastAuthor?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Last Author"),
                            icon = Image(R.drawable.ic_edit),
                            primaryValue = Text(summaryInformation.lastAuthor),
                            removable = true,
                            tag = "lastAuthor"
                        )
                    )
                }

                summaryInformation.subject?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Subject"),
                            icon = Image(R.drawable.ic_subject),
                            primaryValue = Text(summaryInformation.subject),
                            removable = true,
                            tag = "subject"
                        )
                    )
                }

                summaryInformation.keywords?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Keywords"),
                            icon = Image(R.drawable.ic_label),
                            primaryValue = Text(summaryInformation.keywords),
                            removable = true,
                            tag = "keywords"
                        )
                    )
                }

                documentSummaryInformation.category?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Category"),
                            icon = Image(R.drawable.ic_category),
                            primaryValue = Text(documentSummaryInformation.category),
                            removable = true,
                            tag = "category"
                        )
                    )
                }

                summaryInformation.comments?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Comments"),
                            icon = Image(R.drawable.ic_comment),
                            primaryValue = Text(summaryInformation.comments),
                            removable = true,
                            tag = "comments"
                        )
                    )
                }

                summaryInformation.template?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Template"),
                            icon = Image(R.drawable.ic_description),
                            primaryValue = Text(summaryInformation.template),
                            removable = true,
                            tag = "template"
                        )
                    )
                }

                summaryInformation.revNumber?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Revision Number"),
                            icon = Image(R.drawable.ic_history),
                            primaryValue = Text(summaryInformation.revNumber),
                            removable = true,
                            tag = "revNumber"
                        )
                    )
                }

                summaryInformation.applicationName?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Application Name"),
                            icon = Image(R.drawable.ic_apps),
                            primaryValue = Text(summaryInformation.applicationName),
                            removable = true,
                            tag = "applicationName"
                        )
                    )
                }

                summaryInformation.createDateTime?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Created Date"),
                            icon = Image(R.drawable.ic_calendar_today),
                            primaryValue = Text(convertDate(summaryInformation.createDateTime)),
                            removable = true,
                            tag = "createDateTime"
                        )
                    )
                }

                summaryInformation.lastSaveDateTime?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Last Saved Date"),
                            icon = Image(R.drawable.ic_update),
                            primaryValue = Text(convertDate(summaryInformation.lastSaveDateTime)),
                            removable = true,
                            tag = "lastSaveDateTime"
                        )
                    )
                }

                summaryInformation.lastPrinted?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Last Printed"),
                            icon = Image(R.drawable.ic_print),
                            primaryValue = Text(convertDate(summaryInformation.lastPrinted)),
                            removable = true,
                            tag = "lastPrinted"
                        )
                    )
                }

                documentSummaryInformation.company?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Company"),
                            icon = Image(R.drawable.ic_business),
                            primaryValue = Text(documentSummaryInformation.company),
                            removable = true,
                            tag = "company"
                        )
                    )
                }

                documentSummaryInformation.manager?.takeIf { it.isNotBlank() }?.let {
                    metadataList.add(
                        Metadata.Attribute(
                            label = Text("Manager"),
                            icon = Image(R.drawable.ic_supervisor_account),
                            primaryValue = Text(documentSummaryInformation.manager),
                            removable = true,
                            tag = "manager"
                        )
                    )
                }

                workbook.close()
            }
        } catch (e: OfficeXmlFileException) {
            readOOXMLMetadata(context, uri, MediaTypes.MICROSOFT_EXCEL, metadataList)
        }
    }

    private fun removeHSSFMetadata(inputFile: File, outputFile: File, tags: List<String>?) {
        try {
            val workbook = HSSFWorkbook(FileInputStream(inputFile))
            val summaryInformation = workbook.summaryInformation
            val documentSummaryInformation = workbook.documentSummaryInformation
            val date = getStartOfTimeDate()
            val removeAll = tags == null

             summaryInformation.title = ""
            if(removeAll || "author" in tags) summaryInformation.author = ""
            if(removeAll || "lastAuthor" in tags) summaryInformation.lastAuthor = ""
            if(removeAll || "subject" in tags) summaryInformation.subject = ""
            if(removeAll || "keywords" in tags) summaryInformation.keywords = ""
            if(removeAll || "category" in tags) documentSummaryInformation.category = ""
            if(removeAll || "comments" in tags) summaryInformation.comments = ""
            if(removeAll || "template" in tags) summaryInformation.template = ""
            if(removeAll || "revNumber" in tags) summaryInformation.revNumber = ""
            if(removeAll || "applicationName" in tags)summaryInformation.applicationName = ""
            if(removeAll || "createDateTime" in tags)summaryInformation.createDateTime = date
            if(removeAll || "lastSaveDateTime" in tags) summaryInformation.lastSaveDateTime = date
            if(removeAll || "lastPrinted" in tags)summaryInformation.lastPrinted = date
            if(removeAll || "company" in tags)documentSummaryInformation.company = ""
            if(removeAll || "manager" in tags)documentSummaryInformation.manager = ""

            FileOutputStream(outputFile).use { out -> workbook.write(out) }
            workbook.close()
        } catch (e: OfficeXmlFileException) {
            removeOOXMLMetadata(inputFile, outputFile, MediaTypes.MICROSOFT_EXCEL,tags)
        }
    }

    //Open office
    private fun readODFMetadata(
        context: Context,
        uri: Uri,
        metadataList: MutableList<Metadata.Attribute>
    ) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val document = Document.loadDocument(inputStream)
            val metadata = document.officeMetadata

            metadata.initialCreator?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Initial Creator"),
                        icon = Image(R.drawable.ic_person),
                        primaryValue = Text(metadata.initialCreator),
                        removable = true,
                        tag = "initialCreator"
                    )
                )
            }

            metadata.creator?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Creator"),
                        icon = Image(R.drawable.ic_edit),
                        primaryValue = Text(metadata.creator),
                        removable = true,
                        tag = "creator"
                    )
                )
            }

            metadata.editingCycles.toString().takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Editing Cycles"),
                        icon = Image(R.drawable.ic_loop),
                        primaryValue = Text(metadata.editingCycles.toString()),
                        removable = true,
                        tag = "editingCycles"
                    )
                )
            }

            metadata.printedBy?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Printed By"),
                        icon = Image(R.drawable.ic_print),
                        primaryValue = Text(metadata.printedBy),
                        removable = true,
                        tag = "printedBy"
                    )
                )
            }

            metadata.printDate?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Print Date"),
                        icon = Image(R.drawable.ic_print),
                        primaryValue = Text(convertDate(metadata.printDate.time)),
                        removable = true,
                        tag = "printDate"
                    )
                )
            }

            metadata.dcdate?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("DC Date"),
                        icon = Image(R.drawable.ic_update),
                        primaryValue = Text(convertDate(metadata.dcdate.time)),
                        removable = true,
                        tag = "dcdate"
                    )
                )
            }

            metadata.creationDate?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Created Date"),
                        icon = Image(R.drawable.ic_calendar_today),
                        primaryValue = Text(convertDate(metadata.creationDate.time)),
                        removable = true,
                        tag = "creationDate"
                    )
                )
            }

            metadata.language?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Language"),
                        icon = Image(R.drawable.ic_language),
                        primaryValue = Text(metadata.language),
                        removable = true,
                        tag = "language"
                    )
                )
            }

            metadata.keywords.toString().takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Keywords"),
                        icon = Image(R.drawable.ic_label),
                        primaryValue = Text(metadata.keywords.toString()),
                        removable = true,
                        tag = "keywords"
                    )
                )
            }

            metadata.subject?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Subject"),
                        icon = Image(R.drawable.ic_subject),
                        primaryValue = Text(metadata.subject),
                        removable = true,
                        tag = "subject"
                    )
                )
            }

            metadata.description?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Description"),
                        icon = Image(R.drawable.ic_description),
                        primaryValue = Text(metadata.description),
                        removable = true,
                        tag = "description"
                    )
                )
            }

            metadata.generator?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Generator"),
                        icon = Image(R.drawable.ic_build),
                        primaryValue = Text(metadata.generator),
                        removable = true,
                        tag = "generator"
                    )
                )
            }

            document.close()
        }
    }

    private fun removeODSMetadata(inputFile: File, outputFile: File,tags: List<String>?) {
        val spreadsheet = SpreadsheetDocument.loadDocument(inputFile)
        val metadata = spreadsheet.officeMetadata
        val date = getStartOfTimeDate()
        val removeAll = tags == null
        metadata.title = ""
        if(removeAll || "initialCreator" in tags)metadata.initialCreator = ""
        if(removeAll || "creator" in tags)metadata.creator = ""
        if(removeAll || "editingCycles" in tags)metadata.editingCycles = 0
        if(removeAll || "printedBy" in tags)metadata.printedBy = ""
        if(removeAll || "printDate" in tags)metadata.printDate = date.toCalendar()
        if(removeAll || "dcdate" in tags)metadata.dcdate = date.toCalendar()
        if(removeAll || "creationDate" in tags)metadata.creationDate = date.toCalendar()
        if(removeAll || "language" in tags)metadata.language = ""
        if(removeAll || "keywords" in tags)metadata.keywords = emptyList()
        if(removeAll || "subject" in tags)metadata.subject = ""
        if(removeAll || "description" in tags)metadata.description = ""
        if(removeAll || "generator" in tags)metadata.generator = ""

        spreadsheet.save(outputFile)
    }

    private fun removeODTMetadata(inputFile: File, outputFile: File,tags: List<String>?) {
        val document = TextDocument.loadDocument(inputFile)
        val metadata = document.officeMetadata
        val date = getStartOfTimeDate()
        val removeAll = tags == null
       metadata.title = ""
        if(removeAll || "initialCreator" in tags)metadata.initialCreator = ""
        if(removeAll || "creator" in tags)metadata.creator = ""
        if(removeAll || "editingCycles" in tags)metadata.editingCycles = 0
        if(removeAll || "printedBy" in tags)metadata.printedBy = ""
        if(removeAll || "printDate" in tags)metadata.printDate = date.toCalendar()
        if(removeAll || "dcdate" in tags)metadata.dcdate = date.toCalendar()
        if(removeAll || "creationDate" in tags)metadata.creationDate = date.toCalendar()
        if(removeAll || "language" in tags)metadata.language = ""
        if(removeAll || "keywords" in tags)metadata.keywords = emptyList()
        if(removeAll || "subject" in tags)metadata.subject = ""
        if(removeAll || "description" in tags)metadata.description = ""
        if(removeAll || "generator" in tags)metadata.generator = ""

        document.save(outputFile)
    }


    //PDF
    private fun readPDFMetadata(
        context: Context,
        uri: Uri,
        metadataList: MutableList<Metadata.Attribute>
    ) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val document = PDDocument.load(inputStream)
            val info = document.documentInformation

            info.author?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Author"),
                        icon = Image(R.drawable.ic_person),
                        primaryValue = Text(info.author),
                        removable = true,
                        tag = "author"
                    )
                )
            }

            info.subject?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Subject"),
                        icon = Image(R.drawable.ic_subject),
                        primaryValue = Text(info.subject),
                        removable = true,
                        tag = "subject"
                    )
                )
            }

            info.keywords?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Keywords"),
                        icon = Image(R.drawable.ic_label),
                        primaryValue = Text(info.keywords),
                        removable = true,
                        tag = "keywords"
                    )
                )
            }

            info.creator?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Creator"),
                        icon = Image(R.drawable.ic_build),
                        primaryValue = Text(info.creator),
                        removable = true,
                        tag = "creator"
                    )
                )
            }

            info.producer?.takeIf { it.isNotBlank() }?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Producer"),
                        icon = Image(R.drawable.ic_apps),
                        primaryValue = Text(info.producer),
                        removable = true,
                        tag = "producer"
                    )
                )
            }

            info.creationDate?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Created Date"),
                        icon = Image(R.drawable.ic_calendar_today),
                        primaryValue = Text(convertDate(info.creationDate.time)),
                        removable = true,
                        tag = "creationDate"
                    )
                )
            }

            info.modificationDate?.let {
                metadataList.add(
                    Metadata.Attribute(
                        label = Text("Modification Date"),
                        icon = Image(R.drawable.ic_update),
                        primaryValue = Text(convertDate(info.modificationDate.time)),
                        removable = true,
                        tag = "modificationDate"
                    )
                )
            }

            document.close()
        }
    }

    private fun removePDFMetadata(inputFile: File, outputFile: File,tags: List<String>?) {
        FileInputStream(inputFile).use { inputStream ->
            PDDocument.load(inputStream).use { document ->
                val info: PDDocumentInformation = document.documentInformation
                val removeAll = tags == null
                info.title = ""
                if (removeAll || "author" in tags) info.author = ""
                if (removeAll || "subject" in tags) info.subject = ""
                if (removeAll || "keywords" in tags) info.keywords = ""
                if (removeAll || "creator" in tags) info.creator = ""
                if (removeAll || "producer" in tags) info.producer = ""
                if (removeAll || "createdDate" in tags) info.creationDate = null
                if (removeAll || "modificationDate" in tags) info.modificationDate = null

                FileOutputStream(outputFile).use { outputStream ->
                    document.save(outputStream)
                }
            }
        }
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
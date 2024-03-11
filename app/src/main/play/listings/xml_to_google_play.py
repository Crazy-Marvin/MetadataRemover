from pathlib import Path
from bs4 import BeautifulSoup

main_dir = Path(__file__).parent
for lang_dir in main_dir.iterdir():
    if lang_dir.is_file():
        continue

    lang = lang_dir.name
    xml_file = lang_dir / "google_play.xml"

    print(xml_file)

    title_file = lang_dir / "title.txt"
    short_desc_file = lang_dir / "short-description.txt"
    full_desc_file = lang_dir / "full-description.txt"
    
    with xml_file.open("r") as source:
        soup = BeautifulSoup(source, "lxml-xml")
        
        with title_file.open("w") as sink:
            sink.write(soup.content.title.string)
        
        with short_desc_file.open("w") as sink:
            sink.write(soup.content.shortDescription.string)
        
        with full_desc_file.open("w") as sink:
            sink.write(soup.content.fullDescription.string)
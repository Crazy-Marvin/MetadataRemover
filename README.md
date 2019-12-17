[![Icon](art/icons/ic_launcher/legacy/ic_launcher_squircle_xxxhdpi.png)](art/icons/ic_launcher/ic_launcher_play_store.png)

# Metadata Remover App For Android
[![Build Status](https://img.shields.io/github/workflow/status/Crazy-Marvin/MetadataRemover/CI?style=flat)](https://github.com/Crazy-Marvin/MetadataRemover/actions)
[![License](https://img.shields.io/github/license/Crazy-Marvin/MetadataRemover.svg?style=flat)](LICENSE.txt)
[![Last commit](https://img.shields.io/github/last-commit/Crazy-Marvin/MetadataRemover.svg?style=flat)](https://github.com/Crazy-Marvin/MetadataRemover/commits)
[![Releases](https://img.shields.io/github/downloads/Crazy-Marvin/MetadataRemover/total.svg?style=flat)](https://github.com/Crazy-Marvin/MetadataRemover/releases)
[![Latest tag](https://img.shields.io/github/tag/Crazy-Marvin/MetadataRemover.svg?style=flat)](https://github.com/Crazy-Marvin/MetadataRemover/tags)
[![Issues](https://img.shields.io/github/issues/Crazy-Marvin/MetadataRemover.svg?style=flat)](https://github.com/Crazy-Marvin/MetadataRemover/issues)
[![Pull requests](https://img.shields.io/github/issues-pr/Crazy-Marvin/MetadataRemover.svg?style=flat)](https://github.com/Crazy-Marvin/MetadataRemover/pulls)
[![Codacy grade](https://img.shields.io/codacy/grade/eed69c67a07f4a14bf0ee0fd6b2ead40/master.svg?style=flat)](https://www.codacy.com/app/CrazyMarvin/MetadataRemover)
[![Codecov](https://img.shields.io/codecov/c/github/Crazy-Marvin/MetadataRemover/master.svg?style=flat)](https://codecov.io/gh/Crazy-Marvin/MetadataRemover)
[![Crowdin Localization](https://d322cqt584bo4o.cloudfront.net/metadata-remover/localized.svg?style=flat)](https://crowdin.com/project/metadata-remover)
[![F-Droid](https://img.shields.io/f-droid/v/xxxxxxxx.svg?style=flat)](https://f-droid.org/de/packages/xxxxxxxx/)
[![Google Play](https://badgen.net/badge/icon/googleplay?icon=googleplay&label)](https://play.google.com/store/apps/details?id=rocks.poopjournal.metadataremover)

_Remove any image's metadata fast and easily._

<a href="https://play.google.com/store/apps/details?id=rocks.poopjournal.metadataremover">
    <img alt="Get it on Google Play"
        height="80"
        src="https://user-images.githubusercontent.com/15004217/36810046-fa306856-1cc9-11e8-808e-6eb8a81783c7.png" />
        </a>  
<a href="https://f-droid.org/packages/rocks.poopjournal.metadataremover/">
    <img alt="Get it on F-Droid"
        height="80"
        src="https://user-images.githubusercontent.com/15004217/36919296-19b8524e-1e5d-11e8-8962-48463b1cec8a.png" />
        </a>


<i>Protect your privacy by removing metadata from your photos, before sharing them on the internet!</i>

<h2><b>Features:</b></h2>

 ✔️ View metadata
 
 ✔️ Image preview
 
 ✔️ Remove metadata
 
 ✔️ Simple and intuitive interface
 
 ✔️ Share directly from the app
 

<h2><b>Learn more:</b></h2>

Whenever you take a picture, additional metadata is saved in the image file.
Most smartphones do <i>not inform</i> you about this.

<b>Metadata can look like this:</b>

 🕑 On which day was the picture taken, and at which time?
 
 🗺️ And where exactly?
 
 📷 Which camera or which smartphone was used?
 
 🔧 And which camera settings were used?
 
 📝 Notes of the photographer or the camera?
 
 📌 More and more often, even exact GPS coordinates are saved in your photo.
 

Metadata is sometimes very useful—for instance when sorting holiday photos.
But as soon as you share photos with others via social media, all this information is visible <i>publicly</i>.
Data collectors and stalkers would possibly be able to discover your <i>place of residence or workplace<i> from the metadata or draw conclusions about your <i>daily routine</i>.
Tracking services could create more comprehensive advertising profiles and sell your data to other organizations.

With our app, you can easily view all that data, <i>remove it entirely</i>, and then share the anonymized photo directly!
That way you stay <i>anonymous</i> and <i>safe</i> in internet, while your friends can still admire your cute cat.

<i>Happy sharing! 😽</i>

## Deployment

### Encoding secrets

```shell script
cd secrets
tar -cf secrets.tar --exclude=secrets.tar --exclude=secrets.tar.gpg --exclude=.gitignore .
gpg --symmetric --cipher-algo AES256 --output secrets.tar.gpg secrets.tar
```

The same password needs to be saved as `FILES_PASSPHRASE` variable
to this repository's [secrets](https://github.com/Crazy-Marvin/MetadataRemover/settings/secrets).

## Contributing

Check out the [contribution guidelines](CONTRIBUTING.md) on how to help improve this project.

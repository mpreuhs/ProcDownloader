package mip.tools.web

import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

/**
 * Created by Anwender on 2017-02-20.
 */
class Downloader {
    final static def LOGGER = LoggerFactory.getLogger(Downloader.class)

    def targetFolder

    /**
     * Class constructor.
     * @param targetFolder is the local folder name, where to download the file. This URL should ends with "/".
     */
    Downloader(def targetFolder) {
        this.targetFolder = targetFolder
    }

    /**
     * Download method gets a file from sourceUrl and saves it to the folder targetUrl.
     * @param sourceUrl is the file URL to download.
     */
    def download(final downloadUrl) {
        LOGGER.debug('download address:      {}', downloadUrl)

        Path targetFolderPath = Paths.get(this.targetFolder)
        new File(targetFolderPath.toUri()).mkdirs()
        Path targetFilePath = Paths.get(targetFolderPath.toString() + File.separatorChar + this.getFileName(downloadUrl))

        LOGGER.debug('temporary target file: {}', targetFilePath)

        def filename = new FileOutputStream(new File(targetFilePath.toUri()))
        def outputFile = new BufferedOutputStream(filename)

        LOGGER.debug('download start')
        outputFile << new URL(this.fixLocalUrl(downloadUrl)).openStream()
        LOGGER.debug('download complete')

        outputFile.close();
    }

    private String fixLocalUrl(final sourceUrl) {
        def prefixProtocolList = sourceUrl.split(':')

        def protocol
        def fixedSourceUrl
        if (prefixProtocolList.size() == 2) {
            protocol = prefixProtocolList[0]
            fixedSourceUrl = sourceUrl
        } else {
            protocol = 'file'
            fixedSourceUrl = 'file:' + sourceUrl
        }

        LOGGER.info('source url:        {}', sourceUrl)
        LOGGER.debug('fixed source url:  {}', fixedSourceUrl)
        LOGGER.debug('url protocol:      {}', protocol)

        return fixedSourceUrl
    }

    private String getFileName(final url) {
        if (url.endsWith('/')) {
            'index.html'
        } else {
            url.tokenize('/')[-1]
        }
    }

    static void main(def args) {
        def dl = new Downloader('temp/')
        //dl.download('http://www.perrypedia.proc.org/wiki/Die_Dritte_Macht_(Zyklus)')
        dl.download('data/http/www.perrypedia.proc.org/wiki/Die_Dritte_Macht_(Zyklus)')

        Path path = Paths.get('temp/Die_Dritte_Macht_(Zyklus)')
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class)
        LOGGER.debug('file attribute, isRegularFile():  {}', attrs.isRegularFile())    // true
        LOGGER.debug('file attribute, isDirectory():    {}', attrs.isDirectory())      // false
        LOGGER.debug('file attribute, isSymbolicLink(): {}', attrs.isSymbolicLink())   // false
        LOGGER.debug('file attribute, isOther():        {}', attrs.isOther())          // false
        LOGGER.debug('file attribute, size():           {}', attrs.size())             // 59666

        new File('temp/Die_Dritte_Macht_(Zyklus)').delete()
        new File('temp').delete()
    }
}

#! /bin/groovy

import com.synopsys.sigio.util.*

/**
 * Runs a Polaris scan
 * @param manifestYaml
 * @param projectName
 * @return null
 */
def scan(manifest) {
    UtilPrint utilPrint = new UtilPrint()
    utilPrint.sectionSeperator('Running Polaris scan')
    
    def config = manifest.getConfig()
    def projectName = config.gitData.repoOwner + "/" + config.gitData.repoName
    
    def polarisNode = manifest.getConfig('polarisNode')
    node(polarisNode) {

        unstash 'source'
        /* Default: To enable Polaris in the same IO job, uncomment the line below */
        polaris arguments: ' --co project.name=' + projectName + ' --co capture.coverity.autoCapture="enable" analyze -w --coverity-ignore-capture-failure', polarisCli: 'PolarisCLI'
    }
}
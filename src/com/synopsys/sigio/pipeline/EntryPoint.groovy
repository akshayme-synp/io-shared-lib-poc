#! /bin/groovy

package com.synopsys.sigio.pipeline

import com.synopsys.sigio.constants.Constants
import com.synopsys.sigio.model.ToolChain
import com.synopsys.sigio.util.Manifest
import com.synopsys.sigio.util.PipelineUtils
import com.synopsys.sigio.util.DownloadUtils

import java.nio.channels.Pipe

/**
 * This script is the entry point for the pipeline shared library.
 * It defines pipeline stages and manages overall control flow in the pipeline.
 */

/**
 * Begin executing the security pipeline.
 * @param applicationManifest - the application manifest as a YAML file.
 * @param securityManifest - the security manifest as a YAML file.
 * @return null
 */
def execute() {
    node('master') {
        UtilPrint.sectionSeperator('Start Security Pipeline Execution')
        
        // populate gitdata
        def gitData = UtilSCM.getGitRepoInfo()
        
        // git checkout
        UtilSCM.gitCheckout(gitData);       

        // populate config object
        Manifest manifest = new Manifest()
        manifest.init()

        // load io-manifest
        manifest.loadManifest()

        // populate io-manifest with actual values
        manifest.populateManifest()

        // update TPI data (replace assetId and call IO API)
        // manifest.autoOnboardApplication()

        // update calc matrix
        // manifest.autoOnboardRiskMatrix()

        // setToolAndConnectorInfo
        manifest.setToolAndConnectorInfo()

        // stash everything
        stash name: 'source', includes: '**/**'

        // get prescription (call IO API)
        def prescription = manifest.getPrescription()
        
        /* Enable lines below once Workflow client download is permitted over network */
        DownloadUtils downloadUtils = new DownloadUtils();
        downloadUtils.downloadWFClient() 
        stash name: 'workflow-client', includes: '**/WorkflowClient.jar'

        // run security scans
        SecurityPipeline securityPipeline = new SecurityPipeline()
        securityPipeline.execute(manifest, prescription)
    }
}


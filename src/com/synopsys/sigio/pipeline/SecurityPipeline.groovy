package com.synopsys.sigio.pipeline

import com.synopsys.sigio.constants.Constants
import com.synopsys.sigio.model.PipelineTool
import com.synopsys.sigio.util.PipelineUtils
import com.synopsys.sigio.util.StageUtils
import com.synopsys.sigio.util.IOPrescription
import com.synopsys.sigio.util.Manifest

/**
 * Executes the security pipeline for Java projects.
 * @param manifestObj
 * @param toolChain
 * @return
 */
def execute(manifest, prescription) {
    IOPrescription ioPrescription = new IOPrescription()
    StageUtils stageUtils = new StageUtils()
    PipelineUtils pipelineUtils = new PipelineUtils()
    ioConfig = manifest.getConfig()
    def totalRiskScore = ioConfig.totalRiskScore
    //Print IO prescription
    stage(Constants.STAGE_IO_PRESCRIPTION) {
        ioPrescription.printPrescription(ioConfig)
    }

    //SAST
    stage(Constants.STAGE_COMMIT_TIME_CHECKS) {
        UtilPrint.sectionSeperator(Constants.STAGE_COMMIT_TIME_CHECKS)
        if (ioConfig.security.sast.enabled) {
            UtilPrint.sectionSeperator(Constants.STAGE_SAST_ENABLED)          
            UtilPrint.info("Polaris Scan") 
            stageUtils.runPolaris(manifest) 
        } else {
            UtilPrint.sectionSeperator(Constants.STAGE_SAST_DISABLED)
        }
    }

    //SCA
    stage(Constants.STAGE_BUILD_TIME_CHECKS) {
        UtilPrint.sectionSeperator(Constants.STAGE_BUILD_TIME_CHECKS)
        if (ioConfig.security.sca.enabled) {
            UtilPrint.sectionSeperator(Constants.STAGE_SCA_ENABLED)
            UtilPrint.info("Blackduck Scan")   
            stageUtils.runBlackDuck(manifest)
        } else {
            UtilPrint.sectionSeperator(Constants.STAGE_SCA_DISABLED)
        }
    }

    //WORKFLOW
    stage(Constants.STAGE_WORKFLOW) {
        if (ioConfig.security.sast.enabled || ioConfig.security.sca.enabled) {
            UtilPrint.sectionSeperator(Constants.STAGE_WF_ENABLED)
            // update last scan date
            manifest.updateManifestPostScan(prescription)
            // update tool_information
            manifest.updateToolInformation(prescription)
            // call workflow engine
            UtilWorkflow.triggerWorkflow(manifest)
        } else {
            UtilPrint.sectionSeperator(Constants.STAGE_WF_DISABLED)
        }
    }
}
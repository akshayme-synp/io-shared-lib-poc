#! /bin/groovy

import groovy.json.JsonBuilder

def triggerWorkflow(manifest) {

    UtilPrint utilPrint = new UtilPrint()
    ioConfig = manifest.getConfig()

    node('master') {

        unstash 'workflow-client'        

        sh 'ls -la'
        sh 'chmod 755 WorkflowClient.jar'

        def manifestJson = new JsonBuilder(manifest.template_manifest).toPrettyString()
        // utilPrint.debug("Paylod to workflow:\n$manifestJson\n")

        writeFile file: "synopsys-io.json", text: manifestJson

        // sh 'cat synopsys-io.json'
        
        sh 'java -jar WorkflowClient.jar --workflowengine.url=' + env.WORKFLOW_URL.trim() + ' --io.manifest.path=synopsys-io.json'
        def feedbackJson = readJSON file: 'wf-output.json', text: ''
        archiveArtifacts allowEmptyArchive: true, artifacts: '**/wf-output.json'

        utilPrint.debug("Feedback: $feedbackJson")

        if (feedbackJson.breaker.status) {
            def message = 'Build is about to fail, as the following build breaker criteria matched with scan results:\n'
        
            def matched = feedbackJson.breaker.criteria
            if(matched != null) {
                for(int i=0; i< matched.size(); i++) {
                    if(matched[i].activityname == 'sast') {
                            message += 'SAST found:\n ' 
                    } else if(matched[i].activityname == 'sca') {                        
                        message += 'SCA found:\n '
                    }

                    if(matched[i].overall != null)
                        message += 'Overall: ' + matched[i].overall 
                    if(matched[i].categories != null)
                        message+= ' \nCategories: ' + matched[i].categories
                }
            }
            utilPrint.debug(message)
            input message: 'Build breaker criteria matched. Do you want to proceed?', ok: 'Go ahead', submitter: 'skokil'
        }
    }
}

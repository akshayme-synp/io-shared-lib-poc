#! /bin/groovy

import com.synopsys.sigio.model.PipelineTool
import com.synopsys.sigio.util.*

/**
 * Runs a BlackDuck scan on the project
 * @param manifestYaml
 * @param projectName
 * @return null
 */
def scan(manifest) {
    UtilPrint utilPrint = new UtilPrint()
    utilPrint.sectionSeperator('Running BlackDuck SCA')

    synopsys_detect detectProperties: 'detect.detector.search.depth=10 --detect.project.name=${BLACKDUCK_PROJECT_NAME} --detect.project.version.name=${BLACKDUCK_PROJECT_VERSION} --detect.code.location.name=${BLACKDUCK_PROJECT_NAME} --logging.level.detect=DEBUG --detect.project.version.distribution=INTERNAL', downloadStrategyOverride: [$class: 'ScriptOrJarDownloadStrategy'], returnStatus: true
    sleep 120
}
#! /bin/groovy

package com.synopsys.sigio.util

import com.synopsys.sigio.constants.Constants
import com.synopsys.sigio.util.Manifest


/**
 * Runs BlackDuck scan
 * @param manifestObj
 * @param pipelineScan
 * @return null
 */

def runBlackDuck(manifest) {
    SCABlackDuck.scan(manifest)
}

/**
 * Runs Polaris scan
 * @param manifestObj
 * @param pipelineScan
 * @return null
 */
def runPolaris(manifest) {
    SASTPolaris.scan(manifest)
}
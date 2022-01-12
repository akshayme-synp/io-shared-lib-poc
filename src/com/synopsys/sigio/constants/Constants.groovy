package com.synopsys.sigio.constants

/**
 * Maintain shared library constants
 */
class Constants {
    public final static String TOOLCHAIN_SYNOPSYS = 'Synopsys'
    public final static String TOOLCHAIN_OPEN_SOURCE = 'OpenSource'
    public final static String TOOLCHAIN_HYBRID = 'Hybrid'

    public final static String LANGUAGE_JAVA = 'Java'
    public final static String LANGUAGE_RUBY = 'Ruby'
    public final static String LANGUAGE_GO = 'Go'
    public final static String LANGUAGE_PYTHON = 'Python'
    public final static String LANGUAGE_JAVASCRIPT = 'JavaScript'
    public final static String LANGUAGE_REACT = 'React'
    public final static String LANGUAGE_ANGULAR = 'Angular'

    public final static String HTTP_PREFIX = 'http://'
    public final static String LOCALHOST = 'localhost'
    public final static String COLON = ':'
    public final static String F_SLASH = '/'

    public final static String STAGE_IO_PRESCRIPTION = 'IO Prescription'
    public final static String STAGE_SOURCE_CODE_BUILD = 'Build Source Code'
    public final static String STAGE_COMMIT_TIME_CHECKS = 'Commit-Time Checks - SAST'
    public final static String STAGE_BUILD_TIME_CHECKS = 'Build-Time Checks - SCA'
    public final static String STAGE_SAST_COVERITY = 'SAST Coverity'
    public final static String STAGE_SCA_BLACKDUCK = 'SCA BlackDuck'
    public final static String STAGE_SCA_BLACKDUCK_LIGHTWEIGHT = 'SCA BlackDuck - Lightweight'
    public final static String STAGE_SAST_COVERITY_LIGHTWEIGHT = 'SAST Coverity - Lightweight'
    public final static String STAGE_TEST_TIME_CHECKS = 'Test-time Checks'
    public final static String STAGE_TEST_TIME_CHECKS_IAST = 'Test-time Checks - IAST Seeker'
    public final static String STAGE_SONARQUBE = 'Dashboarding'
    public final static String STAGE_IMAGE_SCAN = 'Docker Image Scan'
    public final static String STAGE_WORKFLOW = 'Scan Feedback Workflow'

    public final static String STAGE_SAST_ENABLED = 'SAST is enabled'
    public final static String STAGE_SAST_DISABLED = 'SAST is disabled'
    public final static String STAGE_SCA_ENABLED = 'SCA is enabled'
    public final static String STAGE_SCA_DISABLED = 'SCA is disabled'
    public final static String STAGE_DAST_ENABLED = 'DAST is enabled'
    public final static String STAGE_DAST_DISABLED = 'DAST is disabled'

    public final static String STAGE_WF_ENABLED = 'Running IO WorkflowEngine'
    public final static String STAGE_WF_DISABLED = 'IO WorkflowEngine is disabled'

    public final static String WORKFLOW_CLIENT_DEFAULT_URL_TEMPLATE =
            'https://github.com/synopsys-sig/io-artifacts/releases/download/${WORKFLOW_CLIENT_VERSION}/WorkflowClient.jar'

}

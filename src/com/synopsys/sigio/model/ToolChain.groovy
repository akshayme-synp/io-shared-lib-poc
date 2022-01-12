package com.synopsys.sigio.model

/**
 * Pipeline tool specification.
 */
class ToolChain {
    private PipelineTool sastTool
    private PipelineTool scaTool

    /**
     * Default constructor.
     * Create an open-source pipeline toolchain by default.
     */
    ToolChain() {
        sastTool = PipelineTool.Polaris
        scaTool = PipelineTool.BlackDuck
    }

    PipelineTool getSastTool() {
        return sastTool
    }

    void setSastTool(PipelineTool sastTool) {
        this.sastTool = sastTool
    }

    PipelineTool getScaTool() {
        return scaTool
    }

    void setScaTool(PipelineTool scaTool) {
        this.scaTool = scaTool
    }
}
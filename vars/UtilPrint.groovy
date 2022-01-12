#! /bin/groovy
import groovy.json.JsonBuilder

//--------------------------------------------------------------------------------------
//
// Print/log messages to the Jenkins console log file.
// Usage:
//    UtilPrint.sectionSeperator(msg)
//    UtilPrint.info(msg)
//    UtilPrint.warning(msg)
//    UtilPrint.error(msg)
//    UtilPrint.debug(msg)
//    UtilPrint.colorMsg(msg.color)
//
//--------------------------------------------------------------------------------------

def fgColor = [black     : '\033[30m',
               red       : '\033[31m',
               green     : '\033[32m',
               yellow    : '\033[33m',
               blue      : '\033[34m',
               magenta   : '\033[35m',
               cyan      : '\033[36m',
               white     : '\033[37m',
               reset     : '\033[39m',
               boldBlue  : '\033[1;34m',
               boldPurpel: '\033[1;35m',
               boldYellow: '\033[1;33m']

def bgColor = [black  : '\033[40m',
               red    : '\033[41m',
               green  : '\033[42m',
               yellow : '\033[43m',
               blue   : '\033[44m',
               magenta: '\033[45m',
               cyan   : '\033[46m',
               white  : '\033[47m',
               reset  : '\033[49m']

def style = [bright   : '\033[1m',
             dim      : '\033[2m',
             normal   : '\033[22m',
             reset_all: '\033[0m']
//--------------------------------------------------------------------------------------

def colorMsg(msg, msgColor = '') {
    wrap([$class: 'AnsiColorBuildWrapper']) {
        println msgColor + msg + '\033[0m'
    }
}

def sectionSeperator(section) {
    def msg = "\n====================== " + section + " ==========================\n"
    colorMsg(msg, '\033[34m')
}

def error(msg) { colorMsg(msg, '\033[31m') }

def warning(msg) { colorMsg(msg, '\033[1;33m') }

def info(msg) { colorMsg(msg, '\033[1;34m') }

def data(msg) { colorMsg(msg, '\033[44m') }

def debug(msg) { colorMsg(msg, '\033[1;35m') }

def infoUT(msg) { colorMsg(msg, '\033[40m\033[37m') } // Unit test info color white over black

def map(Map mapObj) {
    def mapStr = new JsonBuilder(mapObj).toPrettyString()
    info(mapStr)
}

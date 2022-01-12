# io-shared-library

Jenkins shared library for integration of Synopsys tools with Jenkins CI

Code Snippet for Jenkinsfile
```
@Library('intelligent-orchestration')
import com.synopsys.*
new com.synopsys.sigio.pipeline.EntryPoint().execute()
```

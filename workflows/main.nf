nextflow.enable.dsl=2

// rm -rf work; nextflow run woundHealing.nf; cat work/*/*/.command.log; rm -rf .next*; rm -rf work

// Default parameters (will be overwritten by CLI input)
//
params.outDirName = 'analysis'
//params.fiji = '/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx' // on tischi's mac
params.fiji = 'fiji' // on jupyter desktop
params.threshold = 25

// Derived parameters
//
inputFiles = params.inputDir + "/*.tif"

// Workflow
//
!{params.fijiScript}
process measureHealing {
  debug true

  input:
    tuple val(key), file(samples)

  shell:
  '''
  echo "Processing dataset:" !{key}
  echo "Contained files:" !{samples}
  echo "Fiji script:" !{params.fijiScript}
  "!{params.fiji}" --ij2 --headless --run "!{params.fijiScript}" 'inputDir="!{params.inputDir}",datasetId="!{key}",threshold="!{params.threshold}",outDirName="!{params.outDirName}",headless="true",saveResults="true"'
  echo "Done: " !{key}
  '''
}


process echoKeys {
  input:
  tuple val(key), file(samples)

  script:
  """
  echo key: $key
  echo images: $samples
  """
}


workflow {
  Channel.fromPath(inputFiles, checkIfExists:true) \
    | map { file ->
      //println "Parsing: " + file.name
      def i = file.name.lastIndexOf("_")
      def key = file.name.substring(0, i)
      return tuple(key, file)
    } \
    | groupTuple() \
    | measureHealing
}

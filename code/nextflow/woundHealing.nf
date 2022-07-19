nextflow.enable.dsl=2

// rm -rf work; nextflow run woundHealing.nf; cat work/*/*/.command.log; rm -rf .next*; rm -rf work

params.inputDir = '/Users/tischer/Documents/daniel-heid-wound-healing/data/input'
//params.inputDir = '/Users/tischer/Desktop/untitled folder/input'
params.fijiScript = '/Users/tischer/Documents/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy'
//params.fiji = '/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx' // tischi's mac
params.fiji = 'fiji' // jupyter desktop

// derived parameters
inputFiles = params.inputDir + "/*.tif"

process measureHealing {
  input:
    tuple val(key), file(samples)

  shell:
  '''
  echo "Processing dataset:" !{key}
  echo "Contained files:" !{samples}
  "!{params.fiji}" --ij2 --headless --run "!{params.fijiScript}" 'inputDir="!{params.inputDir}",datasetId="!{key}",headless="true",saveResults="true"'
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

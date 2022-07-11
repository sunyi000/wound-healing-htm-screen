nextflow.enable.dsl=2

params.inputDir = '/Users/tischer/Documents/daniel-heid-wound-healing/data/input'
params.fijiScript = '/Users/tischer/Documents/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy'
//fiji = '/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx --headless --run' // tischi's mac
params.fiji = 'fiji --headless --run' // jupyter desktop

// derived parameters
inputFiles = params.inputDir + "/*.tif"

process measureHealing {
  input:
    tuple val(key), file(samples)

  shell:
  '''
  echo "Processing dataset:" !{key}
  echo "Contained files:" !{samples}
  !{params.fiji} !{params.fijiScript} "inputDir='!{params.inputDir}',datasetId='!{key}',headless='true'"
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

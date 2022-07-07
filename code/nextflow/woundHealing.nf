nextflow.enable.dsl=2

// input parameters
userDir = '/Users/tischer/Documents' // tischi's mac
//userDir = '??' // jupyter desktop
inputDir = userDir + '/daniel-heid-wound-healing/data/input'
wound_healing_fiji_script = userDir + '/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy'
fiji = '/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx --headless --run' // tischi's mac
// fiji = 'fiji --headless --run' // jupyter desktop

// derived parameters
inputFiles = inputDir + "/*.tif"

process measureHealing {
  input:
    tuple val(key), file(samples)

  shell:
  '''
  echo "Processing dataset:" !{key}
  echo "Contained files:" !{samples}
  !{fiji} !{wound_healing_fiji_script} "inputDir='!{inputDir}',datasetId='!{key}',headless='true'"
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

nextflow.enable.dsl=2

// input parameters
inputDir = '/Users/tischer/Documents/daniel-heid-wound-healing/data/input'
outputDir = '/Users/tischer/Documents/daniel-heid-wound-healing/data/output'
fiji = '/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx --headless --run'
wound_healing_fiji_script = '/Users/tischer/Documents/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy'

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

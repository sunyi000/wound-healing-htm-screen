nextflow.enable.dsl=2

inputFiles = '/Users/tischer/Documents/daniel-heid-wound-healing/data/input/*.tif'

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
      println "Parsing: " + file.name
      def fileNameSplit = file.name.toString().tokenize('_')
      def key = fileNameSplit.get(0) + fileNameSplit.get(1)
      return tuple(key, file)
    } \
    | groupTuple() \
    | echoKeys
}

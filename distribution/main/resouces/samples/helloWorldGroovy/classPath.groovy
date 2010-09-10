def dirName = "../../lib/"
new File(dirName).eachFileMatch(~/.*jar/) { file -> 
    print dirName + file.getName() + ":" 
}  

rm -rf out
mkdir out
javac src/main/java/com/scu/*.java -d out/
cd out
java com/scu/Server -document_root ../src/main/res/com/scu -port 8000
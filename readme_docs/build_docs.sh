./gradlew dokkaGfm
mv build/dokka/gfm .

cd gfm/indico-client-java
rm package-list

# make all relative links absolute

for filename in $(find . -type f); do
  readable_filename=$(echo $filename | sed -r 's|j\-s\-o\-n|json|g; s|\-(\w)\-|\1|g; s|\-(\w[_/])|\1|g; s|\-(\w)$|\1|g; s|_|\-|g; s|/\-|/|g')
  dirname=$(echo $readable_filename | sed -r 's|(.+com\.indico[^/]*)/.+|\1|; s|\./||; s|\.|-|g')
  dirname="./$dirname"
  if [ $(basename $filename) = "index.md" ]; then
    newlocation="$dirname.md"
  else
    mkdir -p $dirname
    newfilename=$(echo $readable_filename | sed -r 's|(.+com\.indico[^/]*/)(.+)|\2|; s|/|\-|g')
    newlocation="$dirname/$newfilename"
  fi
  mv $filename $newlocation
done

mv ../index.md ./index.md

find . -type d -empty -delete
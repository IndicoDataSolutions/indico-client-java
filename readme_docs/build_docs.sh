./gradlew dokkaGfm
mv build/dokka/gfm .

cd gfm/indico-client-java
rm package-list

for filename in $(find . -type f); do
  if [ $(basename $filename) = "index.md" ]; then
    newName=$(dirname $filename | sed -r 's|\-(\w)\-|\1|g; s|/-|/|g; s|-(\w)_|\1-|g; s|_|-|g; s|\./||; s|\.|-|g; s|/|-|2g; s|(.+)\-(\w)$|\1\2|g')
    newName="./$newName.md"
    mkdir -p $(dirname $newName)
    mv $filename $newName
  else
    rm $filename
  fi
done

mv ../index.md ./index.md
# clean up links
find . -type f -name '*.md' -print0 | xargs -0 sed -i -r "s|\[([^]]*)\]\([^)]+\.md[^\)]*\)|\1|g"
find . -type d -empty -delete

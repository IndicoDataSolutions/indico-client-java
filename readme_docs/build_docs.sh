./gradlew dokkaGfm
mv build/dokka/gfm .

cd gfm/indico-client-java
rm package-list

for filename in $(find . -type f); do
  if [ $(basename $filename) = "index.md" ]; then
    # In file names, Dokka separates lower case words with hyphens. It also separates capital letters with hyphens.
    # Since Readme doesn't support capital letters in doc slugs, make all letters lowercase and reserve the hyphen to indicate space between words
    # 1. if a word character has hyphens on each side, remove the hyphens.
    # 2. if a word character occurs between a hyphen and the end of a line, remove the hyphen
    # 3. remove any hyphens that occur after slashes
    # 4. remove last hyphen that occurs before a word break (word breaks are indicated by _ in enums)
    # 5. replace _ with - to represent word breaks-- this mostly applied to enums like PENDING_REVIEW
    # 6. remove the leading ./ that occurs at the beginning of each file so it doesn't get turned into a dash in step 6
    # 7. since readme ignores periods in doc slugs, make any periods into - to make the slugs more readable
    # 8. since readme only supports two levels of docs, transform any / after the first occurrence into a -.
    # This ensures the classes with the same names (like the workflow Builder and submission Builder) will have unique slugs
    newName=$(dirname $filename | sed -r 's|\-(\w)\-|\1|g; s|(.+)\-(\w)$|\1\2|g; s|/-|/|g; s|-(\w)_|\1-|g; s|_|-|g; s|\./||; s|\.|-|g; s|/|-|2g')
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

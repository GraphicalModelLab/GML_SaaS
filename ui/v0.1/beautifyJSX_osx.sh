for file in $(find src -type f)
do
    if [[ $file == *.jsx ]]
    then
       echo "beautify $file"
       echo "$(esformatter --plugins=esformatter-jsx $file)" > $file
    fi
done
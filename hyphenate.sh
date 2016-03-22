#!/bin/bash
#change package name from com.easemob... to com.hyphenate...

#migrate sdk
{
    cd src 
    echo "list files..."
    filelist=$(grep -l "com.hyphenate.easeuix" * -r) 
    
    echo "show files"
    for file in $filelist 
    do
        echo $file
	sed -i -e 's/com.hyphenate.easeuix/com.easemob.easeuix/g' $file
    done
}

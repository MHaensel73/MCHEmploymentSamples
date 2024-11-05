<#
.SYNOPSIS
Create-Subtitles generates .srt subtitle files for all media files in the current folder and all descendent folders.
.DESCRIPTION
Create-Subtitles generates .srt subtitle files for all media files in the current folder and all descendent folders.

To do this:
A list of all files contained in the current folder is generated recursively.
The list is stored in D:\filelist.txt.
The script iterates through the list of media files.
For each media file:
1. Change to the containing directory.
2. Check if a .srt subtitle file has already been created for this media file. If not, generate one.
3. If there’s an error condition, report that.
After all files have been processed, change back to the D:\ directory.

Step 1 is done because the subtitle generation program has a hard time with very long path names. Changing to the parent folder lets us call the subtitle generator with only the file name, leaving off any additional path information.

A media file list is intentionally left in D:\filelist.txt, since it is sometimes necessary to run the script multiple times to complete the operation. It should be deleted manually after successful completion.
.EXAMPLE
Create-Subtitles
#>

# Create Subtitles.ps1
# Creates subtitles for all media files in a given folder

# Where to store list of files
$fileListLocation = 'D:\filelist.txt'

# The script will change the current working directory (CWD) as part of normal operation
# Directory to return to after the script finishes:
$originalDir = 'D:\'

# Create a file list, or load one if it already exists
if (! (Test-Path $fileListLocation)) {
  cd 'D:\Data\Proud Boys Secure Chats'
  $fileList = Get-ChildItem -recurse *.mp4,*.mov,*.avi,*.mkv
  cd $originalDir

  Set-content -Path $fileListLocation  -Value $fileList
} else {
  $fileList=get-content $fileListLocation
}


# For each file in the file list . . .
$fileList| ForEach-Object {

    # Generate the name of the containing directory and expected subtitle file
    $currentFile = Get-Item($_)
    $currentDir = $currentFile.DirectoryName
    $currentFileName = $currentFile.Name
    $subtitleFile = $currentFile.BaseName + '.srt'

    # Temporary debug output
    #echo "File: $currentFile.FullName"
    #echo "subtitle file: $subtitleFile"
      
    # If subtitle file does not exist, make one
    try {

    # Change to the parent directory of the file we're creating subtitles for
    # Whisper-faster has trouble with long paths (>260? characters), and some full paths get very long
    # Changing to the correct directory means whisper-faster only needs to process the file name, no containing path information
    cd $currentDir

    # If the subtitle file does not already exist, create it
    if (! (Test-Path $subtitleFile)) {
        # echo "Creating subtitle file $currentDir\$subtitleFile"
        "D:\Data\PortableApps\Whisper-Faster\whisper-faster.exe --language en --output_dir .\ --model large-v2 '$currentFileName' | Out-Null" | Invoke-Expression 
    }

    # If subtitle file was not created, report an error
    if (! (Test-Path $subtitleFile)) {
       echo "Subtitle file was not created for some reason: $currentDir\$subtitleFile"
    }

    # If subtitle file has length zero, report it
    # If you want to delete zero-length subtitle files automatically, uncomment the # del  line
    if (Test-Path $subtitleFile) {
      if ( (Get-Item $subtitleFile).Length -eq 0) {
        echo "Subtitle file has length zero, should be deleted: $currentDir\$subtitleFile"
        # del $subtitleFile
      }
    }

    }
    catch {
       echo "Unknown error creating subtitles for $subtitleFile"
    }

}

cd $originalDir
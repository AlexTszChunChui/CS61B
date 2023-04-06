# Gitlet

Gitlet is a version-control system similar to Git that allows users to save and track changes to files over time.

## Features

-   `init`: initializes a new Gitlet version control system in the current directory.
-   `add [file]`: adds the file to the staging area.
-   `commit [message]`: saves a snapshot of certain files in the staging area so they can be retrieved later.
-   `rm [file]`: removes the file from the staging area.
-   `log`: prints the commits in the order they were made, along with their commit messages.
-   `global-log`: prints information about all commits ever made.
-   `find [commit message]`: prints out the ids of all commits that have the given commit message.
-   `status`: displays what branches currently exist, and marks the current branch with a `*`.
-   `checkout -- [file]`: restores the file to the most recent commit.
-   `checkout [commit id] -- [file]`: restores the file as it was in the given commit.
-   `checkout [branch name]`: restores all files in the working directory to their versions in the given branch.
-   `branch [branch name]`: creates a new branch with the given name.
-   `rm-branch [branch name]`: deletes the branch with the given name.
-   `reset [commit id]`: checks out all the files tracked by the given commit.

## Usage

To use Gitlet, you can run the following commands in your terminal:

`$ java gitlet.Main [command] [operand]` 

For example, to initialize a new Gitlet repository, run:

`$ java gitlet.Main init` 

For more information on how to use each command, Please visit (https://sp21.datastructur.es/materials/proj/proj2/proj2).

## Notes

-   This project was completed as part of the CS61B course at UC Berkeley.
-   Gitlet was implemented in Java and is designed to run on the command line.
-   This project was created as an exercise in object-oriented programming, and is not intended to be used as a production-level version-control system.
-  Some of the EC features 

However, the following extra credit features have not been implemented:
-   For the `status` command printing the `Modifications Not Staged For Commit` and `Untracked Files` sections
-   Remote commands

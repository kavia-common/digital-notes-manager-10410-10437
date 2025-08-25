#!/bin/bash
cd /home/kavia/workspace/code-generation/digital-notes-manager-10410-10437/notes_app_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi


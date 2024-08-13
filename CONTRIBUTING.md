We follow Git Merge based workflow
1. Fork this repo
2. Create a new feature branch in your fork. Multiple features must have a hyphen separated name
3. Commit to your fork and raise a Pull Request with upstream

## Code Style
- To maintain a consistent code style maven checkstyle plugin is used 
- To inspect, analyze the code to remove common programming defects,
inculcate programming best practices PMD is being used
- The following maven command is used to generate PMD, checkstyle and Copy/Paste Detector (CPD) reports in `./target/site` folder
```mvn checkstyle:checkstyle pmd:pmd pmd:cpd```
- To resolve checkstyle issues faster please install any of the following plugins in the IDE like IntelliJ 
  - google-java-format : [link](https://github.com/google/google-java-format?tab=readme-ov-file)
  - CheckStyle-IDEA : [link](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea)

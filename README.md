# CS172-Project
Search engine-related project

## Phase1: Web Crawler
A crawler that crawls seeds of edu pages  

Sample dataset is stored under src/pages/ directory.  

For Windows:  
To run the crawler, double click crawler.bat  
To add paramters to the command line, open crawler.txt and add parameters.  
Save the file as crawler.bat and run it again.  
  
Parameters: seed.txt number-of-pages max-depth output-dir  
- seed.txt has to be a text file that contains functional links  
- output-dir should be a directory to store downloaded htmls  
  
If one of parameter malfunctions(i.e. invalid data type), the crawler runs in default setting.  

For Linux:  
I could not manage to run java project with 3rd party library on Linux environment.
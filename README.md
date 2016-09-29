RecommendationEngine (A Java Framework for Recommender Systems)
-------------

**RecommendationEngine** is a Java framework for recommender systems (Java version 1.8 or higher required). This framework is Cross-Platform, Open Source , Extensible and Easy to Use. It not only implements state-of-art algorithms but only makes it possible for others to extend it and implement more user-specific algorithms. This framework developed to be used across with [Mise-en-scène Project](http://recsys.deib.polimi.it/?page_id=246).

**Implemented Algorithms:**

 - ItemBased KNN 
 - Average Popularity 
 - Factorization Machine 
 - FunkSVD
  
**Implemeted Features:**

 - Low Level Features (Related to [Mise-en-scène
   Project](http://recsys.deib.polimi.it/?page_id=246)) 
 - Genre 
 - Tag
 - Rating (Collaborative Filtering)

**Running inside Eclipse**

This project is based on [Gradle](https://gradle.org/). So it could be easily imported to Eclipse. For importing it the Eclipse should contain [Buildship Plugin](https://projects.eclipse.org/projects/tools.buildship).  After installing [Buildship Plugin](https://projects.eclipse.org/projects/tools.buildship), you can easily import the project into the Eclipse as a Gradle project.

**Running in Terminal**

For running the project, you only need to modify `config.properties` in `build/install/RecommendationEngine/bin` and then run `build\install\RecommendationEngine\bin\RecommendationEngine.bat` or `build\install\RecommendationEngine\bin\RecommendationEngine.sh`.

**Code structure** 

Public interfaces:

 - `Recommender.java` : All the algorithms should implement this
   interface
 - `AccuracyEvaluation.java` : All the rating prediction metrics (RMSE, MAE, ...) should implement this interface
 - `ListEvaluation.java` : All the list generators metrics (Precision, Recall, ...) should implement this interface

**Configuration**

All user needs to do is changing `config.properties` and then executing the code.
```
#######DATA#############
#If your data has some meta data, please add '#' at the beginning.
#All the lines with "#" will be ignored

#Path to rating file
RATING_FILE_PATH=data/Ratings.csv

#Separator for rating file. 
#Tab=\t
#Semicolon=;
#Comma=,

#RATING_FILE_SEPARATOR=\t
RATING_FILE_SEPARATOR=,

#Path to low level file
#Leave it empty if there is no low level feature file
#LOW_LEVEL_FILE_PATH=
LOW_LEVEL_FILE_PATH=data/LLVisualFeatures13K_QuantileLog.csv
#Separator for low level file
LOW_LEVEL_FILE_SEPARATOR=,

#Path to genre file
#Set empty if there is no genre file
GENRE_FILE_PATH=data/Genre.csv
#Separator for genre file
GENRE_FILE_SEPARATOR=,

#Path to tag file
#Set empty if there is no tag file
TAG_FILE_PATH=data/Tag.csv
#Separator for tag file
TAG_FILE_SEPARATOR=,

#######GENRERAL CONFIGURATION#########

#SIMILARITY_FUNCTION
#possible values: cosine,pearson
SIMILARITY_FUNCTION=cosine

#Number of folds used in cross validation
NUMBER_OF_FOLDS=5

#Number of neighbors used in KNN
NUMBER_OF_NEAREST_NEIGHBOUR=10

#Number of items will be returned in list recommendation
TOP_N=10

#######ALGORITHMS#############

#Number of features used in FactorizationMachine
NUMBER_OF_FEATURES_FOR_FM=10
#Number of iterations used in FactorizationMachine learning
NUMBER_OF_ITERATION_FOR_FM=200
#Learning rate used in FactorizationMachine learning
LEARNING_RATE_FOR_FM=0.001

#Number of features used in FunkSVD
NUMBER_OF_FEATURES_FOR_FUNKSVD=50
#Number of iterations used in FunkSVD
NUMBER_OF_ITERATION_FOR_FUNKSVD=50
#Learning rate used in FunkSVD
LEARNING_RATE_FOR_FUNKSVD=0.005

#######Evaluation metrics#######
#Possible values: MAE,RMSE,PredictionCoverage,NDCG,Precision,Recall
#Can have multiple value (comma separated)
METRICS=MAE,RMSE,PredictionCoverage,NDCG,Precision,Recall

#############RUN CONFIGURATION##################
#Algorithm
#Possible values: ItemBasedNN,FactorizationMachine,AveragePopularity,FunkSVD,HybridTagLowLevel

NUMBER_OF_CONFIGURATION=2

ALGORITHM_1_NAME=FactorizationMachine
ALGORITHM_1_USE_LOW_LEVEL=false
ALGORITHM_1_USE_GENRE=false
ALGORITHM_1_USE_TAG=false
ALGORITHM_1_USE_RATING=true

ALGORITHM_2_NAME=ItemBasedNN
ALGORITHM_2_USE_LOW_LEVEL=true
ALGORITHM_2_USE_GENRE=false
ALGORITHM_2_USE_TAG=false
ALGORITHM_2_USE_RATING=false

ALGORITHM_X_NAME=ItemBasedNN
ALGORITHM_X_USE_LOW_LEVEL=true
ALGORITHM_X_USE_GENRE=false
ALGORITHM_X_USE_TAG=false
ALGORITHM_X_USE_RATING=false
```

**Sample result:**

All the experiments have been done over data in `data` folder.

Rating:

| Algorithm     | RMSE | MAE | Coverage | Precision | Recall | NDCG |
|
| :------- | ----: | :---: |
| ItemBasedKNN | 0.75198567 |  0.5812379 |  0.9809702 | 0.88165295 | 0.46150175 | 0.85933286 | 
| Average Popularity    | 0.87274086 | 0.7008808 | 0.9817214 | 0.9297659  | 0.16849223 | 0.8572529 |
| Factorization Machine (LR=0.05,#Iteration=10,#LatentFactor=10)    | 0.9804152 | 0.7418393 | 0.98041594 | 0.7107271 | 0.50685465 | 0.71443766 |
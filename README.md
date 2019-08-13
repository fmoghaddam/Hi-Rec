Hi-Rec (A Java Framework for Recommender Systems)
-------------

[![Build Status](https://travis-ci.org/fmoghaddam/Hi-Rec.svg?branch=master)](https://travis-ci.org/fmoghaddam/Hi-Rec)

**Hi-Rec** is a Java framework for recommender systems (Java version 1.8 or higher required). This framework is Cross-Platform, Open Source , Extensible and Easy to Use. It not only implements state-of-art algorithms but only makes it possible for others to extend it and implement more user-specific algorithms. This framework developed to be used across with [Mise-en-scène Project](http://recsys.deib.polimi.it/?page_id=246).

**Implemented Algorithms:**

 - ItemBased KNN 
 - Average Popularity 
 - Factorization Machine 
 - FunkSVD
  
**Implemented Metrics:**

 - MAE
 - RMSE
 - Coverage 
 - Precision
 - Recall
 - NDCG
 - Diversity
 - Novelty
 - MAP
  
**Implemented Features:**

 - Low Level Features (Related to [Mise-en-scène
   Project](http://recsys.deib.polimi.it/?page_id=246)) 
 - Genre 
 - Tag
 - Rating (Collaborative Filtering)

**Running inside Eclipse**

This project is based on [Gradle](https://gradle.org/). So it could be easily imported to Eclipse. For importing it the Eclipse should contain [Buildship Plugin](https://projects.eclipse.org/projects/tools.buildship).  After installing [Buildship Plugin](https://projects.eclipse.org/projects/tools.buildship), you can easily import the project into the Eclipse as a Gradle project.

**Running in Terminal**

For running the project, you only need to modify `config.properties` in `build/install/Hi-Rec/bin` and then run `build/install/Hi-Rec/bin/Hi-Rec.bat` or `build/install/Hi-Rec/bin/Hi-Rec.sh`.

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

| Algorithm | RMSE | MAE | Coverage | Precision | Recall | NDCG |
| ------- | ------- | ------- | ------- | ------- | ------- | ------- |
| ItemBasedKNN | 0.75198567 |  0.5812379 |  0.9809702 | 0.88165295 | 0.46150175 | 0.85933286 | 
| Average Popularity    | 0.87274086 | 0.7008808 | 0.9817214 | 0.9297659  | 0.16849223 | 0.8572529 |
| Factorization Machine | 1.0924361 | 0.847448 | 0.9811511 | 0.7052688 | 0.40766105 | 0.7255255 |

Low Level features:

| Algorithm | RMSE | MAE | Coverage | Precision | Recall | NDCG |
| ------- | ------- | ------- | ------- | ------- | ------- | ------- |
| ItemBasedKNN | 0.75276524 |  0.5802754 |  0.98175746 | 0.90045756 | 0.50048786 | 0.861112 | 
| Factorization Machine | 1.2744157 | 1.0030644 | 0.98076487 | 0.71231234 | 0.48030663 | 0.7239859 |

Genre:

| Algorithm | RMSE | MAE | Coverage | Precision | Recall | NDCG |
| ------- | ------- | ------- | ------- | ------- | ------- | ------- |
| ItemBasedKNN | 0.7796999 |  0.6023326 |  0.8747948 | 0.9150926 | 0.46745244 | 0.845462 | 
| Factorization Machine | 1.1264656 | 0.8723874 | 0.98105305 | 0.7536468 | 0.5395745 | 0.75031984 |

Low Level + Genre:

| Algorithm | RMSE | MAE | Coverage | Precision | Recall | NDCG |
| ------- | ------- | ------- | ------- | ------- | ------- | ------- |
| ItemBasedKNN | 0.7572874 |  0.5857588 |  0.982289 |0.89470136 | 0.47212344 | 0.85523474 | 
| Factorization Machine | 1.2027843 | 0.9487314 | 0.9805652 | 0.74588954 | 0.4479124 | 0.7380162 |

FAQ
-------------

### 1. How to run the project without importing it to Eclipse?
As this project is based on Gradle, it can be simply run. If you want to run it without changing any java code, then just do the following steps:
* Download the repository
* In `cmd` or `terminal` go to `build/install/Hi-Rec/bin`
* Change `config.properties` and modify it based on your use case
* Run `Hi-Rec.bat` or `Hi-Rec.sh`

### 2. I want to change the java code and run the project without importing it into Eclipse, How can I do that?
You can open any of the java classes in your favorite editor such as `notepad` and change the code. Then you can build and run the code with the following steps:
* In `cmd` or `terminal` go to the root folder of the project
* Run `gradlew.bat build` or `gradlew.sh build`

In case of any compilation error, you will see the proper error message. If you see `BUILD SUCCESSFUL` you can continue.

* Run `gradlew.bat installApp` or `gradlew.sh installApp`

If you see `BUILD SUCCESSFUL` then you can follow the steps which have been explained in Question 1.

### 3. How to import project into Eclipse?
For importing project into Eclipse, you can use [Buildship Plugin](https://projects.eclipse.org/projects/tools.buildship). For installing this plugin do the following steps:

* Open the Eclipse
* From `Help` menu select `Eclipse MarketPlace`
* Insert `buildship` into search bar and install `Buildship Gradle Integration`

After installing and restarting the Eclipse, you should be able to import the project as a Gradle project.

### 4. How to run the project inside the Eclipse?
For running the project inside the Eclipse you should import it first (Question 3). After importing, from `Gradle Tasks` tab you will be able to select different Gradle tasks. In the simplest scenario, just `build` and `run` task is needed.

### 5. How to implement my own algorithm?
If you need to implement your specific algorithm you only need to create a class in `algorithms` package and extend `Recommender` interface. By doing this, your algorithm will be accessible from `config.properties` file. 

### 6. How to implement my own metric?
If you need to implement another metric, you only need to create a class in `metrics` package and extend one of the `AccuracyEvaluation` or `ListEvaluation` interfaces. By doing this, your metric will be accessible from `config.properties` file. Keep in mind that all the metrics should have `hashCode()` function and this function should return a static fixed number. Currently numbers in [1,6] range occupied. So you can use 7,8,.... You can have a look at the `hashCode()` function in MAE.

### 7. I use this code for my research. Do I have to cite it?
To acknowledge the use of this recommendation engine in your work, please cite the following paper:

```
Mehdi Elahi, Yashar Deldjoo, Farshad Bakhshandegan Moghaddam, LeonardoCella, 
Stefano Cereda, and Paolo Cremonesi. ”Exploring the Semantic Gap 
for Movie Recommendations”. In Proceedings of the 
Eleventh ACM Conference onRecommender Systems. ACM, 326–330, 2017
```

### 8. How can I obtain your dataset?
Low level features which have been collected in [Mise-en-scène Project](http://recsys.deib.polimi.it/?page_id=246) can be downloaded from [this]( https://www.researchgate.net/publication/305682388_Mise-en-Scene_Dataset_Stylistic_Visual_Features_of_Movie_Trailers_description) link. If you need to have Tags, Genre and Ratings, you can use [MovieLens latest dataset](http://grouplens.org/datasets/movielens/latest/). For simplicity we have preproccesed corresponding Tags, Genre and Ratings data and put them in `data` folder.

### 9. How can I give my feedback about the project?
In case of any question or feedback about the project you can use pull requests or you can contact us directly by this email: `f.bakhshandegan@gmail.com`

  

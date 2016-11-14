#!/bin/bash

sudo add-apt-repository ppa:webupd8team/java

sudo apt-get update

sudo apt-get install oracle-java8-installer

wget https://www.dropbox.com/s/n7mq984zf4sqmzm/RecommendationEngine.rar?dl=0

mv RecommendationEngine.rar?dl=0 RecommendationEngine.rar

sudo apt-get install unrar

unrar x -r RecommendationEngine.rar

cd /home/ubuntu/RecommendationEngine/bin

mv RecommendationEngine RecommendationEngine.sh

sudo chmod +x RecommendationEngine.sh

screen
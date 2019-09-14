FROM wernight/phantomjs

USER root
RUN apt update -y
RUN apt install -y python3 python3-pip
RUN apt install -y chromium wget unzip
RUN wget https://chromedriver.storage.googleapis.com/2.41/chromedriver_linux64.zip
RUN unzip chromedriver_linux64.zip
RUN mv chromedriver /usr/bin/chromedriver
RUN chmod +x /usr/bin/chromedriver

RUN pip3 install werkzeug==0.14.1 flask selenium pybars3 pypdf2 flask_wtf
RUN mkdir app
ENTRYPOINT ["python3"]
CMD ["app/app.py"]
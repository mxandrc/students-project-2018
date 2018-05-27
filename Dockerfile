FROM python:3.7-rc

ENV DB_URL "sqlite:///foo.db"

#RUN mkdir /greetings_app

COPY greetings_app/requirements.txt greetings_app/requirements.txt
COPY greetings_app/app.py greetings_app/app.py

RUN pip install -r greetings_app/requirements.txt

CMD python greetings_app/app.py

CREATE TABLE students (
   studentID varchar (9) NOT NULL,
   firstName varchar (25) NOT NULL,
   lastName varchar (25) NOT NULL,
   program varchar (25) NOT NULL,
   PRIMARY KEY (studentID)
);

CREATE TABLE marks (
   markID INT NOT NULL AUTO_INCREMENT,
   mark DOUBLE NOT NULL,
   studentID varchar(9) NOT NULL,
   PRIMARY KEY (markID),
   FOREIGN KEY (studentID) REFERENCES students (studentID)
);

CREATE TABLE nextid (
   nextID INT NOT NULL,
   PRIMARY KEY (nextID)
);

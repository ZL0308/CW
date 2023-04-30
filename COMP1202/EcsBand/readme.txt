# Basic  #

## Part 1 ##
I have implemented the classes Person, Violinist, Cellist, and Pianist, and an interface Musician following the coursework specification
Additional methods, int instrumentID( ), int getseat( ), String getName ( ), are added to the Musician interface. int instrumentID( ) will be used in conductor to fetch the instrumentID of musicians,
int getseat( ) will be used in the orchestra to arrange musicians sit down in the seat, and String getName ( ) will be used in EcsBandAid to match the musician's name with the music score.

## Part 2 ##
I have implemented the classes Orchestra following the coursework specification.

## Part 3 ##
I have implemented the classes Music Score, Conductor and an interface Composition following the coursework
specification

I create an ArrayList<Musician> am, to register new musicians with the conductor.

Additional methods, void removeMusician(Musician m), to let musician leave after performing for a year in EcsBandAid.
			   ArrayList<Musician> contain, to return the collection of the registered musician. 

Firstly, I turn on the SoundSystem.

To assign the scores to the musicians, I use the following algorithm 
 	1. I create a MusicScore[ ] ms to receive whole the MusicScore in a collection.
	2. I create an ArrayList locally which contains the whole of the musicians in am, to avoid the infinite loop that might happen below.
	3. I iterate the MusicScore and musician, while the instrumentID of MusicScore equals the instrumentID of the musician, the musician sits down and reads the MusicScore.
	4. After this, I remove the musician who has sat down from the local ArrayList bm.
	5. Finally, I break the loop which guarantees each Music Score is assigned to one musician.

After playing the composition, I initialize the SoundSystem and turn off the SoundSystem.
	
## Part 4 ##
I have implemented the class MusicSheet following the coursework
specification

Additional member variables, ArrayList<Integer> Arr to receive the notes after transferring, MusicScore m which can be added into MUsicScore, MusicScore [ ] ms, the list of MusicScore,
and another ArrayList<MusicScore> am, to help determine the size of MusicScore [ ].

Additional methods, override a method String getName( ) to return the name of Composition.

To convert from the String representation of notes to the MIDI Ids.
	1. I use a switch to match each note with the correct MIDI lds.
	2. I add the Integer to the ArrayList<Integer> Arr.
	3. I transfer ArrayList into int[ ] in, in the same size.
	4. I create a complete MusicScore and add the name, int [ ] in and the volume boolean soft.
	5. I add whole the MusicScore into an ArrayList am, in order to determine the number of MusicScore.
	6. I create a MusicScore [ ] ms with the same size as the ArrayList am, and move whole the MusicScore from the ArrayList to the MusicScore[ ].

## Part 5 ##
I have implemented the class EcsBandAid following the coursework
specification

Additional local variable, List<Composition> randomC,  to store the 3 random compositions which chosen from the whole composition.

To invite the musicians to the band, I use the following algorithm 
	1. I check the ArrayList in the conductor to find out if there are some musicians staying in the register.
	2. For those musicians who are not in the register or leave the band last after performing for a year, I need to call them back and register them.
	
To let the musician has a 50% chance to leave the band after performing for a year, I use the following algorithm
	1. I use Math. random to generate a number randomly between 0-10, if the number <5, I call the method in the orchestra to stand up, and call the remove method in the 
	conductor to remove the musician from the register, which guarantees a 50% chance.

	
## Part 6 ##
I have implemented the class EcsBandAid following the coursework
specification

To parse the configurations, I definite extra splitter1, and splitter2 classes as
follows.

In splitter 1, I use it to read the musician file and split the musician into the right format which can be received by the EcsBandAid.
I initialize a buffered reader and read the file name. After that, I iterate whole the line, get them and split them into the right format, I add them into an ArrayList and return it.

Similarly, in splitter, I use it to read the composition file and split the composition into the right format. When the reader read and gets the name line which is the first line of the composition, it will 
split it and reassign the name of MusicSheet. Then, it will automatically read the next two lines which must contain the tempo and length of the composition according to the example format of composition.
Meanwhile, I reassign them and create a new MusicSheet. After that, I start to iterate the MusicScore by using the while loop and adding them to the Music Sheet. While the reader gets the line which contains name,
it means that it has finished with one composition, and at this moment, I return the Music Sheet and start to iterate the next composition.

In the main method, I initialize a SoundSystem, and two helper classes, splitter1, splitter2, and an EcsBandAid. The main method can receive three parameters from cmd, the musician's file name, the composition file name and the number 
of years that EcsBandAid supposes to play.
Finally, I iterate the number of years and call the method to perform for a year.

# Extension #
I haimplementedent the following extension.

... Your description of the extension here.
I allow the program to save the current state of a simulation to a file after playing each composition. I let users to choose if they want to stop or continue playing the composition,
users can input the command "Y" yes, or "N" No and the program will determine whether stop or continue. 

I save the current states into two files when the user want to stop. One contains the musicians who haven't leave the Band, the number of compositions which has been played in one year, and the number of years remain.
The other one contains the compositions which have been chosen and have been played.
If the user stops after performing a whole year, we just need to know the musicians who should stayed in the Band and the number if years remain.
If the user stops between a year, we need to count how many compositions have been played, when we reload the Band, we need to stay in the previous year and finish the rest of the compositions.

The following class reader is added ...

It's a helper class which contains a buffered reader. I initialize it and it can receive a file name. 
Just like what we have done before in lab9, 
	I add methods, String getLine to read and get the line in the file.
			     boolean fileIsReady to determine if the file is ready to read.
			      ArrayList<String> getState( ) to read each line of the file and add them into an ArrayList, then return it.

I did some changes in EcsBandAid as follows:
	
Print:
	Firstly, in order to save the current state, I use the print stream to print compositions which have been chosen into the compositionSet.txt file, after playing a composition, the local Integer will count the number of compositions which 
have been played, and then it will also print the composition into the file.

	Secondly, I fetch the ArrayList in the conductor which contains the rest of the musicians after musicians leave the band, I iterate them and print them out into the "State.txt" file. And then, I print the number of compositions which have been 
played into the file. 

	Thirdly, in the main method, I declared a local variable int Years, if the user starts the program for the first time, it is the year which is inputted in the cmd, if it is not the first time, this Years will read the year in the state file.
Run:
	In the main methods, after I run the program, it will first read the file, if the user runs it for the first time, the file is empty and there will be an exception thrown. Then, the program will iterate the year and start to call the perform for a year
	after this, it will print the number of years remaining into the state file.

PerformforAYear:
	before fetching composition from the composition file, it will determine if the compositionSet.txt file is ready.  If the user stopped it before, it will read the file firstly, if the size is 6, which means the user performedform a complete year the and program will 
start next year normally. If size =5 or 4, which means it hasn't finished last year, and now the program will fetch the composition name of last year, add the compositions which haven't been played in last year randomC, meanwhile it will print the 
composition which has been chosen and performed last year into the file. 
	In the play composition part, I gave users a chance to choose if they want to stop playing after playing each composition, I use a while loop which avoided invalid input. the  If user want to stop, a member variable, a boolean will be reassigned, it will break the loop of play composition, 
and in the main method, there is an if( ) that will break the loop by the change of the boolean.

	
Buffered reader:
	The reason why I initialized 6 readers in EcsBandAid is that i need to store the previous state and information, but the read line method can't be reread, so the only way I can solve this situation so far is to initialize a number of readers.
	
Exception:
	When the user uses this program for the first time, there is no content in the file, so the reader can not read anything, and it will throw out about 6 exceptions,

Other details I added in the program as commands.



To run the program with the extension, please use the following
command

java EcsBandAid musicians.txt compositions.txt 10
where
  musicians.txt is the name of the files storing the list of musicians
  compositions.txt is the name of the files storing the list of compositions
  10 is the number of years for simulation.
CREDITS

The initial version of these tables are from:

http://olal.su/index.php?option=com_content&view=article&id=87

Credits goes to the russian former NT team that are behind them, and to Schum who gave some 
helpful input on skill adjustment. And also thanks to google for the translation.

THE FILES

Each skill has a file with its drop speeds. 
The contents are cells for an array that is 13 rows and 8 columns.

The files are loaded when skill drops are intialized, and if there are errors in a file,
a message will appear in the log, and an internal default will be used instead.

Each row indicate the skill level. 

The last row is for skill level 11 and lower.
The one above is for skill level 12.
And the next skill level 13. 
The top row is the hard to reach skill level 23 and higher.

The columns are for the player age.
The left column is age 29 and below
Then follows age 30
... and 31
The last column is for age 36 or higher.

If the value in a column is for instance 2.0, this means the skill for the player
will drop with 0.02 each week, for instance from 12.40 to 12.38.



If you have amazing findings, or are interested in joining in on an effort in tuning the
drop speeds, tune in to the HO CHPP forum at Hattrick:

http://www95.hattrick.org/Forum/Overview.aspx?v=0&f=86471
HO! Rating Prediction
=====================

Please send comments to Flattermann by HT-Mail or email <flattermannHO@gmail.com>.

Overview
========

The rating parameters are split into several files, one for each prediction type (midfield, central attack, side defense...).

All parameter files have the same syntax. The files are case insensitive.

They contain at least one [general] section and one [SKILLNAME] or [SKILLNAME_SIDENAME] section.

SKILLNAME is one of {goalkeeping, defending, playmaking, passing, scoring, winger}.
SIDENAME is one of {allsides, thisside, middle, otherside}
If the SIDENAME is omitted, we take ALLSIDES as default.

Every section has several option=value pairs. (See below for a list of all options)

A hash sign ("#") starts a comment, every character after the # is ignored in this line.

If you want to create your own RatingParameters, copy the directory 'default' to a new name and add that name to the file predictionTypes.conf.
Then, play with the values in midfield.dat, centralattack.dat...

The prediction files are read by HO! everytime he tries to predict the ratings, therefore you can change the values and immediately see the result, without closing and 
reopening HO!.



What's the meaning of SIDENAME?
===============================

As described, the sections are named [SKILLNAME] or [SKILLNAME_SIDENAME].

The SIDENAME is especially usefully for side attack and side defense.
In this context, we have to calculate two different ratings for the left side and the right side.
A player on the left side has - of course - a higher rating impact for the left attack/defense side than for the right side.
If we calculate the right side, THISSIDE means right side and OTHERSIDE means left side.

The players are classified in the following manner:

ALLSIDES:	All Players

MIDDLE:		Keeper, Extra Players,
		Single CA , Single IM, Single FW (if not TowardsWing)

THISSIDE:	CA, IM, FW on this side, if not the only one on this position,
		Wingback and Winger on this side

OTHERSIDE:	Like THISSIDE, for the opposite side



How will the ratings be calculated?
===================================

First, HO! parses the parameter file and seaches for all [SKILLNAME] and [SKILLNAME_SIDENAME] sections.

After that, HO! calculates the player strength for all players on the SIDENAME's side, based on the player's skill, form, stamina and xp using the parameters from the file 
playerstrength.dat.
The PlayerStrength is multiplied with the PlayerWeight from the current section in the parameter file.
For all players, PlayerStrength*PlayerWeight is added up. If there are general options (see below) in this section, they will be applied now.

We do this for every section and sum all these partial ratings together.
Eventually, the general options from the [general] section are applied.



Useable options in the config files
===================================

playerstrength.dat:
~~~~~~~~~~~~~~~~~~~

The PlayerStrength is calculated from these input values: 
skill, stamina, form, xp (experience)

In this file, the options are ALWAYS processed in the following order:

Option				Function

skillSubDeltaForLevelY=x	if (skill = Y AND subskill=0) then skill = skill + x
				(This is used for unknown subskills, i.e. the player did not get training yet and 
				the user did not enter a subskill offset manually)

skillDelta=x			skill = skill + x
staminaDelta=x			stamina = stamina + x
formDelta=x			...
xpDelta=x			...

staminaMin=x			if stamina < x then stamina = x
formMin=x			...
xpMin=x
skillMin=x

staminaMax=x			if stamina > x then stamina = x
formMax=x			...
xpMax=x
skillMax=x

staminaMultiplier=x		stamina = stamina * x
formMultiplier=x		...
xpMultiplier=x
skillMultiplier=x

staminaPower=x			stamina = stamina^x (i.e. stamina raised by x)
formPower=x			...	(Use 0.5 as x for square root)
xpPower=x
skillPower=x

staminaLog=x			stamina = log_x(stamina) (i.e. log of stamina with base x)
formLog=x			...	(Use 10 as x for base-10-log)
xpLog=x
skillLog=x

finalStaminaMultiplier=x	stamina = stamina * x
finalFormMultiplier=x
finalXpMultiplier=x
finalSkillMultiplier=x

finalStaminaDelta=x		stamina = stamina + x
finalFormDelta=x
finalXpDelta=x
finalSkillDelta=x

Now we can calculate the result using these options:
We start with result=skill

resultMultiStamina=x		result = result * x * stamina
resultMultiForm=x		...
resultMultiXp=x

resultAddStamina=x		result = result + x * stamina
resultAddForm=x
resultAddXp=x



rating files (midfield.dat, ...)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
General options (useable in every section, including [general] and [SKILL]):
They will be applied in the following order:

Option			Function
squareMod=x		rating = rating + x*rating*rating 		# dirty hack! Should be 0, if possible
cubeMod=x		rating = rating + x*rating*rating*rating 	# even dirtier hack! Should be 0, if possible

tacticAIM=x		if Tactic==AIM then rating = rating * tacticAOM
tacticAOW=x		if Tactic==AOW then rating = rating * tacticAOW
tacticCounter=x		...
tacticCreative=x
tacticPressing=x

teamSpiritMulti=x	rating = rating * ( 1 + teamSpiritMulti*(YourTeamSpirit-5.5))		# old linear calc - not used anymore

teamSpiritPreMulti=x	rating = rating * (yourTeamSpirit*teamSpiritPreMulti)^teamSpiritPower	# new exponential calc
teamSpiritPower=x

home=x			if game is home game then rating = rating * home
away=x			same for away
awayDerby=x		same for away derby (since HO! 1.401)

pic=x			if you play PIC then rating = rating * pic
mots=x			same for MOTS
normal=x		same for normal

trainerOff=x		if your trainer is offensive then rating = rating * trainerOff
trainerDef=x		same for defensive trainer
trainerNeutral=x	same for neutral trainer

multiplier=x		rating = rating * x
delta=x			rating = rating + x

extraMulti=x		multiplier for extra players (extraIM, extraCD, extraFW)


Player Weights (useable in all [SKILL] sections):

Option			Function
allCDs=x		This weight modifier will be multiplied with every CentralDef
allWBs=x		This weight modifier will be multiplied with every Wingback
allIMs=x		This weight modifier will be multiplied with every InnerMid
allWIs=x		This weight modifier will be multiplied with every Winger
allFWs=x		This weight modifier will be multiplied with every Forward

keeper=x        	keeper

cd_norm=x       	central defender normal
cd_off=x        	central defender offensive
cd_tw=x         	central defender to wing

wb_norm=x       	wingback normal
wb_off=x        	wingback offensive
wb_def=x        	wingback defensive
wb_tm=x         	wingback to middle

im_norm=x       	inner midfield norm
im_off=x        	inner midfield offensive
im_def=x        	inner midfield defensive
im_tw=x         	inner midfield to wing

wi_norm=x       	winger normal
wi_off=x        	winger offensive
wi_def=x        	winger defensive
wi_tm=x        	 	winger to middle

fw_norm=x       	forward normal
fw_def=x        	forward defensive
fw_tw=x         	forward to wing

extra_cd=x      	extra central defender
extra_im=x      	extra inner midfield
extra_fw=x      	extra forward

Every position can have a .SPECIALTY appended, e.g. "fw_def.technical". 
If so, the weight is applied to players with this specialty only.


Example 1 (Midfield)
====================
Let's say, we want to predict the midfield parameters.
We have a midfield.dat with the following text:

[general]
multiplier=0.2
delta=0.8
squareMod=0.05
...

[playmaking]
allIMs=0.5	# Multiplier for ALL IMs
im_normal=1
im_off=0.9
extra_im=0.85
...

Therefore the PlayerStrength of all normal IMs is multiplied with 0.5 * 1 = 0.5, 
the PlayerStrength of all offensive IMs is multiplied with 0.5 * 0.9 = 0.45 and so on.
After that, everything is added up to the PlaymakingSum.

In the end, the parameters from the [general] section are applied to this PlaymakingSum.
I.e. 
PlaymakingSum = PlaymakingSum + squareMod*PlaymakingSum^2
PlaymakingSum = PlaymakingSum * multiplier
PlaymakingSum = PlaymakingSum + delta

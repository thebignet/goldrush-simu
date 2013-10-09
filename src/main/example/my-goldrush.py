#!/usr/bin/env python

import sys
import random

currentPosition = (0, 0)

def printLine(line):
	print line
	sys.stdout.flush()

def initMap(width, height, goldCount):
	pass

def readEnv():
	pos = sys.stdin.readline().split()
	currentPosition = (int(pos[0]), int(pos[1]))
	playerCount = int(pos[2]);
	for y in range(5):
		envLine = sys.stdin.readline().split()
		for x in envLine:
			pass
	for i in range(playerCount):
		playerPosition = sys.stdin.readline().split()

printLine("Python Miner")
line = sys.stdin.readline().split()
initMap(int(line[0]), int(line[1]), int(line[2]))

while True:
	readEnv()
	nextAction = random.choice(["NORTH", "SOUTH", "WEST", "EAST",
			"SHOOT", "PICK", "DROP"])
	printLine(nextAction)
	

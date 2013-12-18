
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

static FILE *log;

typedef enum {
	UNDEFINED,
	HOME,
	MUD,
	STONE,
	EMPTY,
	GOLD,
} CellType;

typedef struct {
	CellType type;
	int value;
} Cell;

typedef struct {
	int x;
	int y;
} Position;

typedef enum {
	NORTH = 0,
	EAST,
	SOUTH,
	WEST,
} Direction;

typedef struct {
	int dx;
	int dy;
} Vector;

Vector directionVector[] = { {0, -1}, {1, 0}, {0, 1}, {-1, 0} };

const char* actions[] = {
			"NORTH", "EAST", "SOUTH", "WEST",
			"SHOOT", "PICK", "DROP"};

int mapWidth;
int mapHeight;
Cell** map;

Position base = { -1, -1};
Position player;
Direction direction = EAST;

Position players[4];

void dolog(const char *fmt, ...) {
	va_list list;
	va_start(list, fmt);
	vfprintf(log, fmt, list);
	fflush(log);
	va_end(list);
}

void input(const char *fmt, ...) {
	va_list list;
	va_start(list, fmt);
	if(vscanf(fmt, list) <= 0) {
		va_end(list);
		dolog("error on read\n");
		exit(1);
	}
	va_end(list);
}

char mapValue(Position *p) {
	switch (map[p->y][p->x].type) {
		case UNDEFINED: return '-';
		case HOME: return 'X';
		case MUD: return 'M';
		case STONE: return 'S';
		case EMPTY: return 'E';
		case GOLD: return 'G';
		default: return ' ';
	}
}

int isInMap(int x, int y) {
	return 0 <= x && x < mapWidth && 0 <= y && y < mapHeight;
}

void setCellInMap(int x, int y, char *value) {
	if (isInMap(x, y)) {
		map[y][x].value = 0;
		CellType type = UNDEFINED;
		if (value[0] == 'X') {
			type = HOME;
		} else if (value[0] == 'M') {
			type = MUD;
		} else if (value[0] == 'S') {
			type = STONE;
		} else if (value[0] == 'E') {
			type = EMPTY;
		} else {
			int n = atoi(value);
			type = GOLD;
			map[y][x].value = n;
		}
		map[y][x].type = type;
		//dolog("cell: %d %d %d %d\n", x, y, map[y][x].type, map[y][x].value);
	}
}

void putLineInMap(int x, int y, char *line) {
	int i = 0;
	char *p = &line[0];
	int xx = -2;
	while (line[i] != '\0') {
		if (line[i] == ' ' || line[i] == '\0') {
			line[i] = '\0';
			setCellInMap(x + xx, y, p);
			p = &line[i+1];
			++xx;
		}
		++i;
	}
	if (p != &line[0]) {
		setCellInMap(x + xx, y, p);
	}
}

void printLine(const char *line) {
	printf("%s\n", line);
	fflush(stdout);
	dolog("out: %s\n", line);
}

void initMap(int width, int height, int goldCount) {
	int y, x;
	dolog("initmap: %d %d %d\n", width, height, goldCount);
	mapWidth = width;
	mapHeight = height;
	map = malloc(sizeof(Cell*)*height);
	for (y = 0; y < height; ++y) {
		map[y] = malloc(sizeof(Cell)*width);
		for (x = 0; x < width; ++x) {
			map[y][x].type = UNDEFINED;
		}
	}
}

void readEnv() {
	int playerCount;
	int y, i;
	input("%d %d %d\n", &player.x, &player.y, &playerCount);
	if (base.x < 0) {
		base.x = player.x;
		base.y = player.y;
	}
	dolog("env: %d %d %d\n", player.x, player.y, playerCount);
	for (y = -2; y <= 2; ++y) {
		char value[30];
		gets(value);
		dolog("line: %s\n", value);
		putLineInMap(player.x, player.y + y, value);
	}
	for (i = 0; i < playerCount; ++i) {
		int px, py;
		input("%d %d\n", &px, &py);
		dolog("player: %d %d\n", px, py);
	}
}

int rotateRight(int d) {
	return (d + 1) % 4;	
}

int rotateLeft(int d) {
	int nd = d - 1;
	return nd < 0 ? 3 : nd;	
}

int cannotGoTo(int x, int y) {
	// XXX cannot go on other players
	return map[y][x].type == STONE
			|| map[y][x].type == HOME && ! (base.x == x && base.y == y);
}

const char *findAction() {
	Position devant = { player.x, player.y };
	Position adroite = { player.x, player.y };
	Position adroitederriere;
	devant.x += directionVector[direction].dx;
	devant.y += directionVector[direction].dy;
	adroite.x += directionVector[rotateRight(direction)].dx;
	adroite.y += directionVector[rotateRight(direction)].dy;
	adroitederriere.x = adroite.x + directionVector[rotateRight(rotateRight(direction))].dx;
	adroitederriere.y = adroite.y + directionVector[rotateRight(rotateRight(direction))].dy;
	
	// _ _ _
	// _ X _ -> Rotate right
	// _ _ S
	
	// _ S _
	// _ X _ -> Rotate left
	// _ _ _
	
	dolog("devant: %d %d %c\n", devant.x, devant.y, mapValue(&devant));

	dolog("adroite: %d %d %c\n", adroite.x, adroite.y, mapValue(&adroite));
	
	dolog("adroitederriere: %d %d %c\n", adroitederriere.x, adroitederriere.y, mapValue(&adroitederriere));
	
	if (! cannotGoTo(adroite.x, adroite.y) && cannotGoTo(adroitederriere.x, adroitederriere.y)) {
		direction = rotateRight(direction);
		dolog("dir: rotate right\n");
	} else if (cannotGoTo(devant.x, devant.y)) {
		direction = rotateLeft(direction);
		dolog("dir: rotate left\n");
	} else {
		dolog("dir: go straight\n");
	}
	return actions[direction];
}

void main() {
	int width, height, goldCount;
	
	log = fopen("log.txt", "w");
	
	printLine("The visitor");
	input("%d %d %d\n", &width, &height, &goldCount);
	initMap(width, height, goldCount);

	while (1) {
		readEnv();
		printLine(findAction());
	}
}
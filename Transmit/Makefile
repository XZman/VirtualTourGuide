NAME	=	gimbalControl
SRCS	=	main.cpp \
			SBGC_mac.cpp \
			sensor.cpp \
			SBGC_cmd_helpers.cpp
OBJS	=	$(SRCS:.cpp=.o)

CC		=	g++
CFLAGS	=	-c -Wall
INC		=	-I/tmp/usr/local/include
LDFLAGS	=	-L/tmp/usr/local/lib
INCLUDE	=	-lserial

.PHONEY: all clean fclean re

all: $(NAME)

$(NAME): $(OBJS)
	$(CC) $(LDFLAGS) -o $(NAME) $(OBJS) $(INCLUDE)

$(OBJS):
	$(CC) $(CFLAGS) $(INC) $(SRCS)

clean:
	rm -rf $(OBJS)

fclean: clean
	rm -rf $(NAME)

re: fclean all

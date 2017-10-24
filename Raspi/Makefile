NAME	=	gimbalControl
SRCS	=	main.cpp \
			SBGC_pi.cpp \
			sensor.cpp \
			SBGC_cmd_helpers.cpp
OBJS	=	$(SRCS:.cpp=.o)

CC		=	g++
CFLAGS	=	-c -Wall
INCLUDE	=	-lwiringPi

.PHONEY: all clean fclean re

all: $(NAME)

$(NAME): $(OBJS)
	$(CC) -o $(NAME) $(OBJS) $(INCLUDE)

$(OBJS):
	$(CC) $(CFLAGS) $(SRCS)

clean:
	rm $(OBJS)

fclean: clean
	rm $(NAME)

re: fclean all

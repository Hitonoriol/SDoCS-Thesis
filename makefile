NAME:=stress-strain
FLAGS:=-Wall -ffixed-line-length-128 -fmax-stack-var-size=1048576 -std=legacy

.PHONY: all test

all: $(NAME)

$(NAME):
	gfortran $(FLAGS) $(NAME).for -o $(NAME)
	chmod +x $(NAME)

lib: $(NAME).so

$(NAME).so:
	gfortran -fPIC -shared $(FLAGS) $(NAME).for -o $(NAME).so

test:
	cat test.in | ./stress-strain

clean:
	rm -f $(NAME) $(addprefix $(NAME),.so .dat .a .bias) zo dzo
NAME:=stress-strain
FLAGS:=-Wall -ffree-form -fno-underscoring -fmax-stack-var-size=1048576 -std=legacy

ifdef DEBUG
	FLAGS+=-g
endif

ifdef PRECISE
	FLAGS+=-fdefault-integer-8 -fdefault-real-8 
endif

.PHONY: all test

all: $(NAME)

$(NAME):
	gfortran $(FLAGS) $(NAME).for -o $(NAME)
	chmod +x $(NAME)

lib: $(NAME).so

$(NAME).so:
	gfortran -fPIC -shared $(FLAGS) $(NAME).for -o $(NAME).so

test:
	@echo "Running test:"; cat test.in
	cat test.in | ./stress-strain

clean:
	rm -f $(NAME) $(addprefix $(NAME),.so .dat .a .bias) zo dzo
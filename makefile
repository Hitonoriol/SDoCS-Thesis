NAME:=stress-strain

$(NAME):
	gfortran -Wall -ffixed-line-length-128 -fmax-stack-var-size=1048576 -std=legacy $(NAME).for -o $(NAME)
	chmod +x $(NAME)

clean:
	rm -f $(NAME) $(NAME).dat $(NAME).a $(NAME).bias zo dzo
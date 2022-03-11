! ------------- Test input -----------------
      program stress_strain
      external calculate
      integer :: n,usl1,usl2
      real :: q1,q2,kpi
      read(5,*) n,q1,q2,kpi,usl1,usl2
      
      call calculate(n,q1,q2,kpi,usl1,usl2)
      end
! ------------------------------------------

!
!           РАСЧЕТ НДС ПРЯМОУГОЛЬНОЙ ПЛАСТИНЫ
!              С ЦИЛИНДРИЧЕСКИМИ ГОФРАМИ
!

      subroutine calculate(n, q1, q2, kpi, usl1, usl2) !bind(C,name='calcStressStrainState')
            character (*), parameter :: NAME = 'stress-strain'

            integer, intent(in) :: n,usl1,usl2
            real, intent(in) :: q1,q2,kpi

            integer m1, ii, zn, l, i, j, k, m, kl, m2, kl1, kl2, kl3, k1
            real x, aa, dlna
            real r, kzd, xzd, yzd
            real alf(30,6)

!            real, dimension(1000) :: xx, yy, xxx, yyy
!            real, dimension(200) :: b1,c1,g
!            real, dimension(200,200) :: a,c
            
            real, dimension(:,:), allocatable :: w, w1, u, mom, qs, ny
            real, dimension(:), allocatable :: f, q, qq, e, b
            real, dimension(:), allocatable :: xx, yy, xxx, yyy
            real, dimension(:), allocatable :: b1,c1,g
            real, dimension(:,:), allocatable :: a,c
            integer :: XYC, AC, QC
            
            allocate(f(n))
            allocate(q(n))
            allocate(qq(n))
            allocate(e(n))
            allocate(b(n))
            
            XYC = 1000
            allocate(xx(XYC))
            allocate(yy(XYC))
            allocate(xxx(XYC))
            allocate(yyy(XYC))
            
            AC = n * 6
            allocate(b1(AC))
            allocate(c1(AC))
            allocate(g(AC))
            allocate(a(AC, AC))
            allocate(c(AC, AC))

            write(6,*) 'КОЛ-ВО СЕКЦИЙ: ',n
            h=1.
            aa=50.
            
            QC = n + int((aa*3.14/kpi) / 10) * 2
            write(*,*) 'Alloc [',n,',',QC,']'
            allocate(w(n, QC))
            allocate(w1(n, QC))
            allocate(u(n, QC))
            allocate(mom(n, QC))
            allocate(qs(n, QC))
            allocate(ny(n, QC))
            
            write(6,*) 'ИНТЕНСИВНОСТЬ ВНЕШНЕЙ НОРМАЛЬНОЙ '
            write(6,*) 'НАГРУЗКИ НА ЧЕТНОЙ И НЕЧЕТНОЙ ОБОЛОЧКЕ: ',q1,', ',q2
            do 2 i=1,n,2
2           q(i)=q1
            do 22 i=2,n,2
22          q(i)=q2
            write(6,*) 'КОЭФФИЦИЕНТ НА КОТОРЫЙ ДЕЛИТСЯ УГОЛ PI: ',kpi
            do 3 i=1,n
            e(i)=200.
3           b(i)=0.25
            do 4 i=1,n
4           f(i)=(1.-b(i)**2)/e(i)/h*q(i)
            k=n*6
            do 7 i=1,k
            do 7 j=1,k
            a(i,j)=0.
            b1(i)=0.
            c1(i)=0.
            c(i,j)=0.
7           g(i)=0.
            do 11 i=1,n
            do 11 j=1,17
            u(i,j)=0.
            w(i,j)=0.
            w1(i,j)=0.
            mom(i,j)=0.
            qs(i,j)=0.
11          ny(i,j)=0.

            write(6,*) ' СИММЕТРИЯ СЛЕВА [0/1]: ',usl1
            if(usl1.eq.1) goto 3001

            x=-aa*3.14/kpi

            a(1,1)=1.
            a(1,2)=x
            a(1,3)=x*x
            a(1,4)=2.*x*x*x+h**2*x
            a(1,5)=cos(x/aa)
            a(1,6)=sin(x/aa)
            b1(1)=0.

            a(2,1)=0.
            a(2,2)=-aa
            a(2,3)=-2.*aa*x
            a(2,4)=-6.*aa*x*x
            a(2,5)=sin(x/aa)
            a(2,6)=-cos(x/aa)
            b1(2)=-aa**2*f(1)

            a(3,1)=0.
            a(3,2)=0.
            a(3,3)=-2.*aa
            a(3,4)=-12.*aa*x
            a(3,5)=cos(x/aa)/aa
            a(3,6)=sin(x/aa)/aa
            b1(3)=0.
            goto 3011

3001        x=0.

            a(1,1)=1.
            a(1,2)=x
            a(1,3)=x*x
            a(1,4)=2.*x*x*x+h**2*x
            a(1,5)=cos(x/aa)
            a(1,6)=sin(x/aa)
            b1(1)=0.

            a(2,1)=0.
            a(2,2)=0.
            a(2,3)=0.
            a(2,4)=0.
            a(2,5)=-cos(x/aa)*(1./aa**3)
            a(2,6)=-sin(x/aa)*(1./aa**3)
            b1(2)=0.

            a(3,1)=0.
            a(3,2)=0.
            a(3,3)=-2.*aa
            a(3,4)=-12.*aa*x
            a(3,5)=cos(x/aa)/aa
            a(3,6)=sin(x/aa)/aa
            b1(3)=0.

3011        zn=-1
            m1=1
            i=4
            m=1
            j=1
            l=1
1002        qq(m)=-e(m)*h**3/12./(1.-b(m)**2)
            qq(m+1)=-e(m+1)*h**3/12./(1.-b(m+1)**2)
            zn=-1*zn
            x=aa*3.14/kpi*zn

            a(i,j)=0.
            a(i,j+1)=-aa
            a(i,j+2)=-2.*aa*x
            a(i,j+3)=-6.*aa*x*x
            a(i,j+4)=sin(x/aa)
            a(i,j+5)=-cos(x/aa)

            a(i,j+6)=0.
            a(i,j+7)=-aa
            a(i,j+8)=-2.*aa*x
            a(i,j+9)=-6.*aa*x*x
            a(i,j+10)=sin(x/aa)
            a(i,j+11)=-cos(x/aa)
            b1(i)= -aa**2*(f(l)+f(l+1))

            a(i+1,j)=1.
            a(i+1,j+1)=x
            a(i+1,j+2)=x*x
            a(i+1,j+3)=2.*x*x*x+h**2*x
            a(i+1,j+4)=cos(x/aa)
            a(i+1,j+5)=sin(x/aa)

            a(i+1,j+6)=1.
            a(i+1,j+7)=x
            a(i+1,j+8)=x*x
            a(i+1,j+9)=2.*x*x*x+h**2*x
            a(i+1,j+10)=cos(x/aa)
            a(i+1,j+11)=sin(x/aa)
            b1(i+1)=0.

            a(i+2,j)=0.
            a(i+2,j+1)=0.
            a(i+2,j+2)=-2.*aa
            a(i+2,j+3)=-12.*aa*x
            a(i+2,j+4)=cos(x/aa)/aa
            a(i+2,j+5)=sin(x/aa)/aa

            a(i+2,j+6)=0.
            a(i+2,j+7)=0.
            a(i+2,j+8)=2.*aa
            a(i+2,j+9)=12.*aa*x
            a(i+2,j+10)=-cos(x/aa)/aa
            a(i+2,j+11)=-sin(x/aa)/aa
            b1(i+2)=0.

            a(i+3,j)=0.
            a(i+3,j+1)=qq(m)*(-1./aa)
            a(i+3,j+2)=qq(m)*(-2.*x/aa)
            a(i+3,j+3)=qq(m)*(-12.*aa-(6.*x*x+h**2)/aa)
            a(i+3,j+4)=0.
            a(i+3,j+5)=0.

            a(i+3,j+6)=0.
            a(i+3,j+7)=-qq(m+1)*(-1./aa)
            a(i+3,j+8)=-qq(m+1)*(-2.*x/aa)
            a(i+3,j+9)=-qq(m+1)*(-12.*aa-(6.*x*x+h**2)/aa)
            a(i+3,j+10)=0.
            a(i+3,j+11)=0.

            b1(i+3)=0.

            a(i+4,j)=0.
            a(i+4,j+1)=0.
            a(i+4,j+2)=qq(m)*(-2./aa)
            a(i+4,j+3)=qq(m)*(-12.*x/aa)
            a(i+4,j+4)=0.
            a(i+4,j+5)=0.

            a(i+4,j+6)=0.
            a(i+4,j+7)=0.
            a(i+4,j+8)=qq(m+1)*(-2./aa)
            a(i+4,j+9)=qq(m+1)*(-12.*x/aa)
            a(i+4,j+10)=0.
            a(i+4,j+11)=0.

            b1(i+4)=0.

            a(i+5,j)=0.
            a(i+5,j+1)=0.
            a(i+5,j+2)=0.
            a(i+5,j+3)=-qq(m)*12.
            a(i+5,j+4)=0.
            a(i+5,j+5)=0.

            a(i+5,j+6)=0.
            a(i+5,j+7)=0.
            a(i+5,j+8)=0.
            a(i+5,j+9)=-qq(m+1)*12.
            a(i+5,j+10)=0.
            a(i+5,j+11)=0.

            b1(i+5)=qq(m)*12./h**2*aa*f(l)+qq(m+1)*12./h**2*aa*f(l+1)

            if(m1.ge.n-1) goto 1001
            m1=m1+1
            write(*,*)'m1=',m1
            i=i+6
            j=j+6
            l=l+1
            m=m+1
            goto 1002

1001        write(6,*) ' СИММЕТРИЯ СПРАВА [0/1]: ',usl2
            if(usl2.eq.1) goto 3002

            x=-aa*3.14/kpi

            a(i+6,j+6)=1.
            a(i+6,j+7)=x
            a(i+6,j+8)=x*x
            a(i+6,j+9)=2.*x*x*x+h**2*x
            a(i+6,j+10)=cos(x/aa)
            a(i+6,j+11)=sin(x/aa)
            b1(i+6)=0.

            a(i+7,j+6)=0.
            a(i+7,j+7)=-aa
            a(i+7,j+8)=-2.*aa*x
            a(i+7,j+9)=-6.*aa*x*x
            a(i+7,j+10)=sin(x/aa)
            a(i+7,j+11)=-cos(x/aa)
            b1(i+7)=-aa**2*f(n)

            a(i+8,j+6)=0.
            a(i+8,j+7)=0.
            a(i+8,j+8)=-2.*aa
            a(i+8,j+9)=-12.*aa*x
            a(i+8,j+10)=cos(x/aa)/aa
            a(i+8,j+11)=sin(x/aa)/aa
            b1(i+8)=0.
            goto 3022

3002        x=0.

            a(i+6,j+6)=1.
            a(i+6,j+7)=x
            a(i+6,j+8)=x*x
            a(i+6,j+9)=2.*x*x*x+h**2*x
            a(i+6,j+10)=cos(x/aa)
            a(i+6,j+11)=sin(x/aa)
            b1(i+6)=0.

            a(i+7,j+6)=0.
            a(i+7,j+7)=0.
            a(i+7,j+8)=0.
            a(i+7,j+9)=0.
            a(i+7,j+10)=-cos(x/aa)*(1./aa**3)
            a(i+7,j+11)=-sin(x/aa)*(1./aa**3)
            b1(i+7)=0.

            a(i+8,j+6)=0.
            a(i+8,j+7)=0.
            a(i+8,j+8)=-2.*aa
            a(i+8,j+9)=-12.*aa*x
            a(i+8,j+10)=cos(x/aa)/aa
            a(i+8,j+11)=sin(x/aa)/aa
            b1(i+8)=0.

3022        k=n*6
            open(unit=9,file=NAME//'.a')
            do 111 i=1,k
            do 112 j=1,k
112         write(9,90) 'a(',i,',',j,')=',a(i,j)
111         write(9,91) 'b1(',i,')=',b1(i)
90          format(a2,i2,a1,i2,a2,f20.5)
91          format(a2,i2,a2,f20.5)
            close(unit=9)
            call gauss(k,a,b1,c1,c,g)

            write(*,*) 'm=', m
            x=-aa*3.14/kpi
            l=0
            i=1
            m=1
            if(usl1.eq.1) x=0.
10          qq(m)=-e(m)*h**3/12./(1.-b(m)**2)

            write(*,*) 'm=',m,'; i=',i
            w(m,i)=c1(l+2)*(-aa)+c1(l+3)*(-2.*aa*x)+c1(l+4)*(-6.*aa*x*x)+c1(l+5)*sin(x/aa)+c1(l+6)*(-cos(x/aa))+aa**2*f(m)
            w1(m,i)=c1(l+3)*(-2.*aa)+c1(l+4)*(-12.*aa*x)+c1(l+5)*cos(x/aa)/aa+c1(l+6)*sin(x/aa)/aa
            u(m,i)=c1(l+1)+c1(l+2)*x+c1(l+3)*x*x+c1(l+4)*(2.*x*x*x+h**2*x)+c1(l+5)*cos(x/aa)+c1(l+6)*sin(x/aa)
            mom(m,i)=qq(m)*(c1(l+2)*(-1./aa)+c1(l+3)*(-2.*x/aa)+c1(l+4)*(-12.*aa-(6.*x*x+h**2)/aa))
            qs(m,i)=qq(m)*(c1(l+3)*(-2./aa)+c1(l+4)*(-12.*x/aa))
            ny(m,i)=-qq(m)*12./h**2*(c1(l+4)*h**2+aa*f(m))

            i=i+1
            x=x+10.
            if(x.le.(aa*3.14/kpi)) goto 10
            if(usl1.eq.1.and.m.eq.1) kl1=i-1
            if(usl2.eq.1.and.m.eq.n) kl2=i-1
            if(m.eq.2) kl3=i-1
            if(m.ge.n) goto 9
            m=m+1
            i=1
            l=l+6
            x=-aa*3.14/kpi
            if(usl2.eq.1.and.m.eq.n) x=0.
            goto 10

9           open(unit=40,file=NAME//'.dat')
            write(*,*) '* Writing to .dat file'
            i=1
            x=-aa*3.14/kpi
            m1=1
            m2=2
            kl=kl3
            if(usl1.eq.1) x=0.
            if(usl1.eq.1) kl=kl1
            write(40,500) '  q1=',q(1),  '  q2=',q(2),  ' kpi=',kpi
            write(40,501) ' usl1=',usl1, ' usl2=',usl2
500         format(3(a5,f6.3))
501         format(2(a6,i3))
            write(40,*)'-------------------------------------------------------------------------------------'
            write(40,*)'|  x  |    U(x)    |    W(x)    |   W^(x)    |    N(x)    |    M(x)    |    Q(x)    |'
            write(40,*)'-------------------------------------------------------------------------------------'
451         write(40,400)' |',x,'|',u(m1,i),'|',w(m1,i),'|',w1(m1,i),'|',ny(m1,i),'|',mom(m1,i),'|',qs(m1,i),'|'
            x=x+30.
            i=i+3
            if(x.le.(aa*3.14/kpi)) goto 451
            x=aa*3.14/kpi
            i=kl
            write(40,400)' |',x,'|',u(m1,i),'|',w(m1,i),'|',w1(m1,i),'|',ny(m1,i),'|',mom(m1,i),'|',qs(m1,i),'|'

!           Вычисление погрешностей
            alp1=abs(u(m1,i))
            alp2=abs(u(m2,i))
            alf(m1,1)=abs(alp1-alp2)

            alp1=abs(w(m1,i))
            alp2=abs(w(m2,i))
            alf(m1,2)=abs(alp1-alp2)

            alp1=abs(w1(m1,i))
            alp2=abs(w1(m2,i))
            alf(m1,3)=abs(alp1-alp2)

            alp1=abs(ny(m1,i))
            alp2=abs(ny(m2,i))
            alf(m1,4)=abs(alp1-alp2)

            alp1=abs(mom(m1,i))
            alp2=abs(mom(m2,i))
            alf(m1,5)=abs(alp1-alp2)

            alp1=abs(qs(m1,i))
            alp2=abs(qs(m2,i))
            alf(m1,6)=abs(alp1-alp2)

            kl=kl3
            if(usl2.eq.1.and.m2.eq.n) kl=kl2
            ii=kl
            write(40,*) ' '
452         write(40,400)' |',x,'|',u(m2,ii),'|',w(m2,ii),'|',w1(m2,ii),'|',ny(m2,ii),'|',mom(m2,ii),'|',qs(m2,ii),'|'
            x=x-30.
            ii=ii-3
            dlna=-aa*3.14/kpi
            if(usl2.eq.1.and.m2.eq.n) dlna=0.
            if(x.ge.dlna) goto 452
            x=-aa*3.14/kpi
            if(usl2.eq.1.and.m2.eq.n) x=0.
            ii=1
            write(40,400)' |',x,'|',u(m2,ii),'|',w(m2,ii),'|',w1(m2,ii),'|',ny(m2,ii),'|',mom(m2,ii),'|',qs(m2,ii),'|'

            m1=m1+2
            m2=m2+2
            x=-aa*3.14/kpi
            i=1
            write(40,*) ' '
            if(m2.le.n) goto 451
            write(40,*)'-------------------------------------------------------------------------------------'
400         format(a2,f5.1,a1,6(f12.6,a1))
            close(unit=40)
            write(*,*) 'Done writing to .dat file'

            open(unit=41,file=NAME//'.bias')
            m1=1
            write(41,500) '  q1=',q(1),  '  q2=',q(2),  ' kpi=',kpi
            write(41,501) ' usl1=',usl1, ' usl2=',usl2
            write(41,*)
            write(41,*)
            write(41,*)'            Погрешности стыковки (абсолютные)'
            write(41,*)      '-------------------------------------------------------------------'
            write(41,*)      '|   U - U  |   W - W  |  W^ - W^ |   N - N  |  M - M  |   Q - Q  |'
            write(41,*)      '-------------------------------------------------------------------'

453         write(41,503) ' |',alf(m1,1),'|',alf(m1,2),'|',alf(m1,3),'|',alf(m1,4),'|',alf(m1,5),'|',alf(m1,6),'|'
503         format(a2,6(e10.3,a1))
            m1=m1+1
            if(m1.le.(n-1)) goto 453
            write(41,*)      '--------------------------------------------------------------------'
            close(unit=41)

            open(unit=16,file='zo')
            i=1
            k1=0
            m1=1
            r=aa
            fi=-3.14/kpi
            kzd=1.
            yzd=aa*cos(3.14/kpi)
            xzd=aa*(1.-sin(3.14/kpi))
56          xx(i)=k1*r+r+r*sin(fi)-kzd*xzd
            yy(i)=r*cos(fi)-yzd
            write(16,*) xx(i),yy(i)
            fi=fi+10./aa
            i=i+1
            if(fi.le.3.14/kpi) goto 56
            k1=k1+2
            kzd=kzd+2.
            fi=-3.14/kpi
66          xx(i)=k1*r+r+r*sin(fi)-kzd*xzd
            yy(i)=-r*cos(fi)+yzd
            write(16,*) xx(i),yy(i)
            fi=fi+10./aa
            i=i+1
            if(fi.le.3.14/kpi) goto 66
            kzd=kzd+2.
            fi=-3.14/kpi
            k1=k1+2
            m1=m1+2
            if(m1.le.n) goto 56
            close(unit=16)

            open(unit=17,file='dzo')
            i=1
            k1=0
            m1=1
            m2=2
            ii=1
            r=aa
            fi=-3.14/kpi
            kzd=1.
57          xxx(i)=k1*r+r+(r+w(m1,ii))*sin(fi+u(m1,ii)/r)-kzd*xzd
            yyy(i)=(r+w(m1,ii))*cos(fi+u(m1,ii)/r)-yzd
            write(17,*) xxx(i),yyy(i)
            fi=fi+10./aa
            i=i+1
            ii=ii+1
            if(fi.le.3.14/kpi) goto 57
            k1=k1+2
            kzd=kzd+2.
            fi=-3.14/kpi
            ii=kl
67          xxx(i)=k1*r+r+(r+w(m2,ii))*sin(fi-u(m2,ii)/r)-kzd*xzd
            yyy(i)=-(r+w(m2,ii))*cos(fi-u(m2,ii)/r)+yzd
            write(17,*) xxx(i),yyy(i)
            fi=fi+10./aa
            i=i+1
            ii=ii-1
            if(fi.le.3.14/kpi) goto 67
            ii=1
            kzd=kzd+2.
            fi=-3.14/kpi
            k1=k1+2
            m1=m1+2
            m2=m2+2
            if(m1.le.n) goto 57
            close(unit=17)
            
            deallocate(b1)
            deallocate(c1)
            deallocate(g)
            deallocate(a)
            deallocate(c)
            
            return
      end

      subroutine gauss(n,a,b,x,c,g)
            character(25) :: fleft

            real, dimension(n, n) :: a, c
            real, dimension(n) :: b, x, g
            real v,s

            n1=n-1
            do 4 k=1,n1
            if(abs(a(k,k)).gt.0.) goto 2
            k1=k+1
            do 3 m=k1,n
            if(abs(a(m,k)).gt.0.) goto 1
            goto 3
1           do 7 l=1,n
            v=a(k,l)
            a(k,l)=a(m,l)
7           a(m,l)=v
            v=b(k)
            b(k)=b(m)
            b(m)=v
3           continue
2           g(k)=b(k)/a(k,k)
            k1=k+1
            do 4 i=k1,n
            b(i)=b(i)-a(i,k)*g(k)
            do 4 j1=k,n
            j=n-j1+k
            c(k,j)=a(k,j)/a(k,k)
4           a(i,j)=a(i,j)-a(i,k)*c(k,j)

            m=n
            x(m)=b(m)/a(m,m)
5           m=m-1
            s=0.
            do 6 l=m,n1
6           s=s+c(m,l+1)*x(l+1)
            x(m)=g(m)-s
            if(m.gt.1) goto 5

            do 20 i=1,n
20          write(6,'("x[",I3.1,"] = ",A)') i,fleft(x(i))

            r=1.
            do 50 l=1,n
50          r=r*a(l,l)
            write(6,*) 'opr = ',fleft(r)
            return
      end

      function fleft(num)
            real :: num
            character(25) :: fleft
            write(fleft, '(F25.8)') num
            fleft = adjustl(fleft)
      end
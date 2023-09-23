#include<iostream>
#include<fstream>
using namespace std;
int main()
{
    cout<<"                                               "<<"Welcome to Daily Mart"<<endl;
    cout<<endl;






        cout<<"1.Stock Management : 1.Add new product     [For access,please enter '11']"<<endl;
        cout<<"                     2.Update Stock        [For access,please enter '12']"<<endl;
        cout<<"                     3.Stock report        [For access,please enter '13']"<<endl;



        cout<<endl;
        cout<<"2.Sells Management : 1.Generate a bill     [For access,please enter '21']"<<endl;
        cout<<"                     2.Ex-change product   [For access,please enter '22']"<<endl;
        cout<<"                     3.Sells report        [For access,please enter '23']"<<endl;


        cout<<endl;
        cout<<"For access,please enter a code : "<<endl;
        int  code;
        cin>>code;
        switch (code)
        {
        case 11:
            /* code */
            break;
        case 12:

           break;
        case 13:
           break;
        case 21:

           break;  

        case 22:
            break;
        case 23:
             break;           

        
        default:
        cout<<"You have to press only 11 12 13 21 22 or 23";
            break;
        }
}
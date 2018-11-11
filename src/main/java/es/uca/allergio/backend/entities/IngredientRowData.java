package es.uca.allergio.backend.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class IngredientRowData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer a,b,c,d,e,f,g,h,i,j,k,l,m,n,ñ,o,p,q,r,s,t,u,v,w,x,y,z;
    String ingredient;

    @ManyToOne(cascade = CascadeType.PERSIST)
    public Ingredient originalIngredient;

    public IngredientRowData(String ingredientName, Integer... instance) {
        this.ingredient = ingredientName;
        this.a = instance[0]; this.b = instance[1]; this.c = instance[2];
        this.d = instance[3]; this.e = instance[4]; this.f = instance[5];
        this.g = instance[6]; this.h = instance[7]; this.i = instance[8];
        this.j = instance[9]; this.k = instance[10]; this.l = instance[11];
        this.m = instance[12]; this.n = instance[13]; this.ñ = instance[14];
        this.o = instance[15]; this.p = instance[16]; this.q = instance[17];
        this.r = instance[18]; this.s = instance[19]; this.t = instance[20];
        this.u = instance[21]; this.v = instance[22]; this.w = instance[23];
        this.x = instance[24]; this.y = instance[25]; this.z = instance[26];
    }
}

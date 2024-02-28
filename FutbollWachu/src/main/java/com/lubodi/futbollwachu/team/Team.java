package com.lubodi.futbollwachu.team;

import org.bukkit.Material;

public enum Team {
    RED("Red", Material.RED_BANNER),
    BLUE("Blue",Material.BLUE_BANNER);

    private  String  display;
    private Material material;
    Team(String name, Material material){
        this.display = name;
        this.material = material;
    }
    public String getDisplay() {return  display;}
    public Material getMaterial() {return  material;}


}

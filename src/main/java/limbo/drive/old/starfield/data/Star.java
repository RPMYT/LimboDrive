package limbo.drive.old.starfield.data;

import net.minecraft.nbt.NbtCompound;

public record Star(String name, double mass, double temperature, int quadrant, int cluster, int position) {
    public NbtCompound serialize() {
        NbtCompound compound = new NbtCompound();

        compound.putString("Name", this.name);
        compound.putDouble("Mass", this.mass);
        compound.putDouble("Temperature", this.temperature);
        compound.putInt("Quadrant", this.quadrant);
        compound.putInt("Cluster", this.cluster);
        compound.putInt("Position", this.position);

        return compound;
    }

    public static Star deserialize(NbtCompound compound) {
        return new Star(
            compound.getString("Name"),
            compound.getDouble("Mass"),
            compound.getDouble("Temperature"),
            compound.getInt("Quadrant"),
            compound.getInt("Cluster"),
            compound.getInt("Position")
        );
    }
}
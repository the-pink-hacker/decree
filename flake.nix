{
    inputs = {
        nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
        flake-utils.url = "github:numtide/flake-utils";
    };
    outputs = {
        nixpkgs,
        flake-utils,
        ...
    }:
        flake-utils.lib.eachDefaultSystem (system: let
            pkgs = import nixpkgs {
                inherit system;
            };
            lib = pkgs.lib;
        in {
            formatter = pkgs.alejandra;
            devShells.default = pkgs.mkShell {
                buildInputs = with pkgs; [
                    gradle_9
                ];
                XDG_DATA_DIRS = "${pkgs.gsettings-desktop-schemas}/share/gsettings-schemas/${pkgs.gsettings-desktop-schemas.name}:${pkgs.gtk3}/share/gsettings-schemas/${pkgs.gtk3.name}:$XDG_DATA_DIRS";
                LD_LIBRARY_PATH = lib.makeLibraryPath (with pkgs; [
                    glfw3-minecraft
                    libglvnd
                    udev
                    pulseaudio
                ]);
            };
        });
}

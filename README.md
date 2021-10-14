# EmailVerification
Allows for server owners to setup e-mail verification for their minecraft servers.

### Description

EmailVerification is a plugin that allows server owners to setup e-mail verification for their Minecraft servers.

The plugin allows users to enter their mail info through the [config.yml](https://github.com/DuppyIsCool/EmailVerification/blob/master/EV/src/main/resources/config.yml) alongside the message customization.

Users can also restrict the emails to certain extensions. Allowing only users with emails associated with their organization (such as schools or businesses) to join.

Users who are not authenticated are still able to join the server, however will only be able to move around and view the world. Users who are not authenticated also get removed from the server if there is no room for an authenticated player. 

Currently this has only been tested with TLS connections via google smtp. Future updates may include support for other connections and better message customization through HTML formatting. In addition, more updates may be made

Users are currently stored locally in the players.yml, however I was thinking of implementing SQL support in the future.

### Dependencies

This program implements [JavaMail](https://javaee.github.io/javamail/) version 1.6.2

In addition, this plugin is meant to be run [Spigot servers](https://www.spigotmc.org/)
## Commands & Permissions

This plugin introduces 2 Commands

/authorize (email) - This will send a verification e-mail to the provided mail address if it is a valid address.

/code (code) - Users enter their code to authorize their account.

emailauth.bypass - Allows a user to be considered authenticated without completing the email verification process.

## Authors

[DuppyIsCool | KL](https://github.com/DuppyIsCool)

## Version History

* This Project was compiled with Spigot 1.17.1

# Developer Documentation

## Force a specific Token and Token Secret

The Token and Token Secret can be overridden by environment variables.

| Environment Variable        | Description                    |
|-----------------------------|--------------------------------|
| `HO_ENCRYPTED_TOKEN`        | the **encrypted** token        |
| `HO_ENCRYPTED_TOKEN_SECRET` | the **encrypted** token secret |

If these environment variables are set then every time the Token and Token Secret is needed for a connection to Hattrick
they are prior used to the values from the database (especially when there is no database during development).

### How to get the encrypted values from the database

1. Go to the menu `Debug`
2. Choose `SQL Editor`
3. Enter
   ```
   select * from USERCONFIGURATION where config_key = 'AccessToken';
   ```
   and press F5.
4. The value of `CONFIG_VALUE` shows the encrypted **Token** (e.g `ABC`).
5. Set this value in your environment to force it everytime
   ```
   # e.g.: export HO_ENCRYPTED_TOKEN="ABC"
   export HO_ENCRYPTED_TOKEN="[CONFIG_VALUE_FOR_ACCESS_TOKEN]"
   ```
6. Enter
   ```
   select * from USERCONFIGURATION where config_key = 'TokenSecret';
   ```
   and press F5.
7. The value of `CONFIG_VALUE` shows the encrypted **Token Secret** (e.g `DEF`).
8. Set this value in your environment to force it everytime
   ```
   # e.g.: export HO_ENCRYPTED_TOKEN_SECRET="DEF"
   export HO_ENCRYPTED_TOKEN_SECRET="[CONFIG_VALUE_FOR_TOKEN_SECRET]"
   ```

## Activate the "Save Downloaded XML" on startup

The `Save downloaded XML` option can be controlled on startup using an environment variable.

| Environment Variable     | Description                                  |
|--------------------------|----------------------------------------------|
| `HO_SAVE_DOWNLOADED_XML` | `true` (case-insensitive) or `1` to activate |

If the environment variable is set to `true` (case-insensitive) or `1`, the `Debug` / `Save downloaded XML` option is
activated on startup.

Any other value deactivates the option. If the environment variable is not set, the option is not changed.

Example:

```
export HO_SAVE_DOWNLOADED_XML=true
```

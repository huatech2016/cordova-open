#import "Open.h"

@implementation Open

/**
 *  open
 *
 *  @param command An array of arguments passed from javascript
 */
- (void)open:(CDVInvokedUrlCommand *)command {

  // Check command.arguments here.
  //[self.commandDelegate runInBackground:^{
    dispatch_async(dispatch_get_main_queue(), ^{
        // Call UI related operations
   
    CDVPluginResult* commandResult = nil;
    NSString *path = [command.arguments objectAtIndex:0];

    if (path != nil && [path length] > 0) {
        
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES);
        NSString *libraryDirectory = [paths objectAtIndex:0];
        NSLog(@"app_home_lib: %@",libraryDirectory);
        
        NSString *newPath =  [[NSString alloc] initWithFormat:@"%@%@%@",libraryDirectory,@"/files/huatechTemp/",path];
        NSLog(@"newpath: %@",newPath);
        
        
      //NSURL *url = [NSURL URLWithString:newPath];
        NSURL *url = [NSURL fileURLWithPath:newPath];
        //NSURL * url = [NSURL URLWithString:[newPath stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]]];
      NSError *err;

      if (url.isFileURL &&
          [url checkResourceIsReachableAndReturnError:&err] == YES) {

        self.fileUrl = url;

        QLPreviewController *previewCtrl = [[QLPreviewController alloc] init];
        previewCtrl.delegate = self;
        previewCtrl.dataSource = self;
          
        [previewCtrl.navigationItem setRightBarButtonItem:nil];
          
        [self.viewController presentViewController:previewCtrl animated:YES completion:nil];

        NSLog(@"cordova.disusered.open - Success!");
        commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                          messageAsString:@""];

      } else {
        NSLog(@"cordova.disusered.open - Invalid file URL wq   ");
        //commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
          commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:0];
      }
    } else {
      NSLog(@"cordova.disusered.open - Missing URL argument");
      commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:commandResult
                                callbackId:command.callbackId];
 // }];
         });
}

#pragma - QLPreviewControllerDataSource Protocol

- (NSInteger)numberOfPreviewItemsInPreviewController:
                 (QLPreviewController *)controller {
  return 1;
}

- (id<QLPreviewItem>)previewController:(QLPreviewController *)controller
                    previewItemAtIndex:(NSInteger)index {
  return self;
}

#pragma - QLPreviewItem Protocol

- (NSURL *)previewItemURL {
  return self.fileUrl;
}

@end

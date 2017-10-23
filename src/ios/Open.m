#import "Open.h"

@implementation Open

/**
 *  open
 *
 *  @param command An array of arguments passed from javascript
 */
- (void)open:(CDVInvokedUrlCommand *)command {


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
     NSURL *url = [NSURL fileURLWithPath:newPath];
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
          commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:0];
      }
    } else {
      NSLog(@"cordova.disusered.open - Missing URL argument");
      commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:commandResult callbackId:command.callbackId];
         });
}

-(void)isFileExist:(CDVInvokedUrlCommand*)command{
    NSArray  *arguments = command.arguments;
    NSString *fileId;
    NSString  *fileName;
    NSString  *extension;
    if (!arguments || [arguments count] < 2) {
        NSLog(@"#### setTagsWithAlias param is less");
        return ;
    }else{
        fileId = arguments[0];
        fileName  = arguments[1];
        extension =  [fileName pathExtension];  //aaa.doc -> doc
    }
     NSString *storeName =[NSString stringWithFormat:@"%@.%@",fileId,extension];
    //NSLog(@"####  fileId is %@, fileName is %@",fileId,fileName);
    //NSString *tmpDir=[NSHomeDirectory() stringByAppendingPathComponent:@"tmp/files"];
   // NSString *storFile = [tmpDir stringByAppendingPathComponent:[NSString stringWithFormat:@"/%@",storeName]];

    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES);
           NSString *libraryDirectory = [paths objectAtIndex:0];
           NSLog(@"app_home_lib: %@",libraryDirectory);
           NSString *newPath =  [[NSString alloc] initWithFormat:@"%@%@%@",libraryDirectory,@"/files/huatechTemp/",storeName];
        NSLog(@"newpath: %@",newPath);
        NSURL *storFile = [NSURL fileURLWithPath:newPath];


   NSFileManager *fileManager = [NSFileManager defaultManager];
   BOOL result = [fileManager fileExistsAtPath:storFile];
    CDVPluginResult* pluginResult = nil;

   if (result) {
         pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
     } else {
         pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
     }


       [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

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

